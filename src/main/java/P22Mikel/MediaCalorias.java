package P22Mikel;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
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

public class MediaCalorias {

    private static class MapClass extends Mapper<LongWritable, Text, Text, LongWritable> {

        private Text restaurante = new Text();
        private LongWritable calorias = new LongWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                // Expresión regular para dividir considerando comas dentro de comillas
                String[] linea = value.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (linea.length > 2) {
                    String restauranteStr = linea[0];
                    
                    //hay 2 registros de Mcdonald's que tienen Calorie Swetener como Company
                    if (restauranteStr.startsWith("Calorie Sweetener")) {
                        restaurante.set("McDonald’s");
                    } else {
                        restaurante.set(restauranteStr);

                    }

                    String caloriasStr = linea[2].replaceAll("[,\"]", "").trim(); // Elimina comas y comillas

                    if (!caloriasStr.isEmpty() && !caloriasStr.equals("Calories")) {
                        try {
                            int caloriasInt = Integer.parseInt(caloriasStr);
                            calorias.set(caloriasInt);

                            context.write(restaurante, calorias);
                        } catch (NumberFormatException e) {
                            // Si no se puede convertir a número, escribir 0 calorías
                            context.write(restaurante, new LongWritable(0));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("CAPTURADA " + e.getMessage());
                System.err.println("EXCEPCION " + e);
                e.printStackTrace(System.err);
            }
        }
    }

    private static class PartitionerClass extends Partitioner<Text, LongWritable> {

        @Override
        public int getPartition(Text key, LongWritable value, int numReduceTasks) {

            String restaurante = key.toString();
            if (restaurante.startsWith("McDonald")) {
                return 0;
            } else if (restaurante.startsWith("Burger")) {
                return 1;
            } else if (restaurante.startsWith("Wendy")) {
                return 2;
            } else if (restaurante.startsWith("KFC")) {
                return 3;
            } else if (restaurante.startsWith("Taco")) {
                return 4;
            } else {
                return 5;
            }
        }
    }

    private static class ReduceClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private Text restaurante = new Text();
        private LongWritable sumcalorias = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {
            long sum = 0L;
            long cont = 0L;

            for (LongWritable caloriaProducto : values) {
                sum += caloriaProducto.get();
                cont++;
            }

            if (cont != 0) {
                sumcalorias.set(sum / cont);
                context.write(key, sumcalorias);
            } else {
                context.write(key, new LongWritable(0));  // Manejo en caso de que no haya valores
            }
        }
    }

    public static void main(String[] args) {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("a_83036");
        try {
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://192.168.10.1:9000");
                    Job job = Job.getInstance(conf, "MediaCalorias");

                    job.setJarByClass(MediaCalorias.class);
                    job.setMapperClass(MapClass.class);
                    job.setPartitionerClass(PartitionerClass.class);
                    job.setReducerClass(ReduceClass.class);

                    job.setNumReduceTasks(6);

                    job.setOutputKeyClass(Text.class);
                    job.setOutputValueClass(LongWritable.class);

                    FileInputFormat.addInputPath(job, new Path("/PCD2024/a_83036/fastfood_data"));
                    FileOutputFormat.setOutputPath(job, new Path("/PCD2024/a_83036/fastfood_result_MediaCals"));

                    boolean finalizado = job.waitForCompletion(true);
                    System.out.println("Finalizado: " + finalizado);
                    return null;
                }
            });
        } catch (Exception e) {
            System.err.println("Capturada excepcion: " + e);
            System.err.println("Mnesaje: " + e.getMessage());
            e.printStackTrace(System.err);
        }

    }

}
