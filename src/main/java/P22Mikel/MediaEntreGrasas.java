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

public class MediaEntreGrasas {

    private static class MapClass extends Mapper<LongWritable, Text, Text, LongWritable> {

        private Text restaurante = new Text();
        private LongWritable mediaGrasas = new LongWritable();

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

                    String totGrasas = linea[4].replaceAll("[,\"]", "").trim(); // Elimina comas y comillas
                    String GrasasSatu = linea[5].replaceAll("[,\"]", "").trim();
                    String GrasasTrans = linea[6].replaceAll("[,\"]", "").trim();
                    String[] grasas = {totGrasas, GrasasSatu, GrasasTrans};
                    if (!totGrasas.isEmpty() && !GrasasSatu.isEmpty() && !GrasasTrans.isEmpty()
                            && !totGrasas.equals("\"Total Fat(g)\"")
                            && !GrasasSatu.equals("\"Saturated Fat(g)\"")
                            && !GrasasTrans.equals("\"Trans Fat(g)\"")) {
                        try {
                            int totGrasasInt = Integer.parseInt(totGrasas);
                            int GrasasSatuInt = Integer.parseInt(GrasasSatu);
                            int GrasasTransInt = Integer.parseInt(GrasasTrans);
                            if (totGrasasInt > 0 || GrasasSatuInt > 0 || GrasasTransInt > 0) {
                                int mediaGrasasInt = (totGrasasInt + GrasasSatuInt + GrasasTransInt) / 3;
                                mediaGrasas.set(mediaGrasasInt);
                                System.out.println("Clave: " + restauranteStr);
                                System.out.println("Valor: " + mediaGrasasInt);
                                context.write(restaurante, mediaGrasas);
                            }

                        } catch (NumberFormatException e) {

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
        private LongWritable mediaGrasasTot = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {
            long sum = 0L;
            long cont = 0L;

            for (LongWritable media3grasas : values) {
                sum += media3grasas.get();
                cont++;
            }

            if (cont != 0) {
                mediaGrasasTot.set(sum / cont);
                context.write(key, mediaGrasasTot);
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
                    Job job = Job.getInstance(conf, "MediaEntreGrasas");

                    job.setJarByClass(MediaEntreGrasas.class);
                    job.setMapperClass(MapClass.class);
                    job.setPartitionerClass(PartitionerClass.class);
                    job.setReducerClass(ReduceClass.class);

                    job.setNumReduceTasks(6);

                    job.setOutputKeyClass(Text.class);
                    job.setOutputValueClass(LongWritable.class);

                    FileInputFormat.addInputPath(job, new Path("/PCD2024/a_83036/fastfood_data"));
                    FileOutputFormat.setOutputPath(job, new Path("/PCD2024/a_83036/fastfood_result_MediaGrasas"));

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
