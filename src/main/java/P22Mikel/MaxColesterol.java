package P22Mikel;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Scanner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;

public class MaxColesterol {

    public static class MapClass extends Mapper<LongWritable, Text, Text, Text> {

        private Text restaurante = new Text();
        private Text productoColesterol = new Text();
        private String userRestaurante;

        @Override
        protected void setup(Context context) {
            userRestaurante = context.getConfiguration().get("restaurante");
            System.out.println("aqui: " + userRestaurante);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                String[] linea = value.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (linea.length > 7) {
                    String restauranteStr = linea[0];
                    // Filter based on user input
                    if (!restauranteStr.startsWith(userRestaurante)) {
                        return;
                    }
                    restaurante.set(restauranteStr);

                    String productoStr = linea[1];
                    String colesterolStr = linea[7].replaceAll("[,\"]", "").trim();

                    if (!colesterolStr.isEmpty() && !colesterolStr.equals("Cholesterol")) {
                        productoColesterol.set(productoStr + "," + colesterolStr);
                        context.write(restaurante, productoColesterol);
                    }
                }
            } catch (Exception e) {
                System.err.println("Exception in Map: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    public static class ReduceClass extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String maxProducto = "";
            int maxColesterol = Integer.MIN_VALUE;

            for (Text val : values) {
                String[] productoColesterol = val.toString().split(",", -1);
                String producto = productoColesterol[0];
                String colesterolStr = productoColesterol[1];
                System.out.println("produuctoColesterol=" + producto + "," + colesterolStr);
                if (colesterolStr.contains("<")) {
                    colesterolStr = colesterolStr.replace("<", "");
                }
                if (colesterolStr.contains(" ")) {
                    colesterolStr = colesterolStr.replace(" ", "");
                }

                try {
                    if (!colesterolStr.isEmpty() && !colesterolStr.equals(" ") && !colesterolStr.equals("")) {
                        int colesterol = Integer.parseInt(colesterolStr);

                        if (colesterol > maxColesterol) {
                            System.out.println(colesterol);
                            maxColesterol = colesterol;
                            maxProducto = producto;
                        }
                    }

                } catch (NumberFormatException e) {
                    System.err.println("CAPTURADA " + e.getMessage());
                    e.printStackTrace(System.err);
                }

            }

            result.set(maxProducto + "," + maxColesterol);
            context.write(key, result);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el nombre del restaurante (McDonald, Burger, Wendy, KFC, Taco, Otro):");
        String restaurante = scanner.nextLine();

        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("a_83036");
        try {
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://192.168.10.1:9000");
                    conf.set("restaurante", restaurante);
                    Job job = Job.getInstance(conf, "MaxColesterol");

                    job.setJarByClass(MaxColesterol.class);
                    job.setMapperClass(MapClass.class);
                    job.setReducerClass(ReduceClass.class);
                    job.setNumReduceTasks(1);

                    job.setOutputKeyClass(Text.class);
                    job.setOutputValueClass(Text.class);

                    FileInputFormat.addInputPath(job, new Path("/PCD2024/a_83036/fastfood_data"));
                    FileOutputFormat.setOutputPath(job, new Path("/PCD2024/a_83036/fastfood_result_MaxColesterol"));

                    boolean finalizado = job.waitForCompletion(true);
                    System.out.println("Finalizado: " + finalizado);
                    return null;
                }
            });
        } catch (Exception e) {
            System.err.println("Capturada excepcion: " + e);
            System.err.println("Mnesaje: " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            scanner.close();
        }

    }

}
