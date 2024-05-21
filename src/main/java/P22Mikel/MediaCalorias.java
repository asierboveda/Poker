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

    //Mapeador --> la salida será: (Restaurante, caloriaProducto) 
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

                    //Fase de conseguir las calorias del producto
                    String caloriasStr = linea[2].replaceAll("[,\\\"\\s]", "").trim(); // Elimina comas y comillas
                    caloriasStr=caloriasStr.replaceAll(" ", "");
                    if (!caloriasStr.isEmpty() && !caloriasStr.equals("Calories")) {//evitar la cabecera y los valores de caloria nulos
                        try {
                            int caloriasInt = Integer.parseInt(caloriasStr);
                            System.out.println("Clave="+restauranteStr+" Valor="+caloriasInt);
                            calorias.set(caloriasInt);
                            context.write(restaurante, calorias);
                        } catch (NumberFormatException e) {
                            System.err.println("EXCEPCION: " + e);
                            System.err.println("CAPTURADA " + e.getMessage());
                            e.printStackTrace(System.err);
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

    //Particionador-->queremos diseccionar el trabajo en 6 subtareas independientes, las cuales tendrán los pares (clave,valor)=(restaurante,mediaGrasas)
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

    //Reductor --> se va a conseguir 1 par clave-valor (en cada subtarea) (restaurante, MEDIA de calorias)
    private static class ReduceClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private Text restaurante = new Text();
        private LongWritable sumcalorias = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {
            long sum = 0L;
            long cont = 0L;

            //Construir la media de las calorias que presentan los productos en la casilla VALOR de cada par
            for (LongWritable caloriaProducto : values) {
                sum += caloriaProducto.get();
                cont++;
            }

            sumcalorias.set(sum / cont);
            context.write(key, sumcalorias);

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
