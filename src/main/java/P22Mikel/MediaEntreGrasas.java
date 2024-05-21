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

    //Mapeador --> vamos a querer la salida: (Restaurante, Media grasas) para cada producto
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
                    //hay 2 registros de Mcdonald's que tienen Calorie Swetener como Company --> se arregla la clave(restaurante)
                    if (restauranteStr.startsWith("Calorie Sweetener")) {
                        restaurante.set("McDonald’s");
                    } else {
                        restaurante.set(restauranteStr);

                    }

                    //Fase de construcción del valor --> MEDIA DE 3 TIPOS DE GRASAS
                    String totGrasas = linea[4].replaceAll("[,\\\"\\s]", "").trim(); // Elimina comas, comillas y espacios
                    String GrasasSatu = linea[5].replaceAll("[,\\\"\\s]", "").trim();
                    String GrasasTrans = linea[6].replaceAll("[,\\\"\\s]", "").trim();
                    //comprobación cabecera o valor nulo
                    if (!totGrasas.isEmpty() && !GrasasSatu.isEmpty() && !GrasasTrans.isEmpty()
                            && !totGrasas.equals("\"Total Fat(g)\"")
                            && !GrasasSatu.equals("\"Saturated Fat(g)\"")
                            && !GrasasTrans.equals("\"Trans Fat(g)\"")){
                        //try para controlar los errores de tener un String q no se pueda hacer entero (" ")
                        try {
                            double totGrasasDouble = Double.parseDouble(totGrasas);
                            double GrasasSatuDouble = Double.parseDouble(GrasasSatu);
                            double GrasasTransDouble = Double.parseDouble(GrasasTrans);

                            /*este if se realiza porque grasasSatu y GasasTrans presentan muchos productos con valor vacío o cero
                              y al hacer la media de las 3 grasas, queremos valores representativos*/
                            if (totGrasasDouble > 0 && (GrasasSatuDouble > 0 || GrasasTransDouble > 0)) {
                                double mediaGrasasDouble = (totGrasasDouble + GrasasSatuDouble + GrasasTransDouble) / 3;
                                mediaGrasas.set((long) mediaGrasasDouble);
                                System.out.print("Clave: " + restauranteStr);
                                System.out.println(", Valor: " + mediaGrasasDouble);
                                context.write(restaurante, mediaGrasas);
                            }

                        } catch (NumberFormatException e) {
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

    //Reductor--> se quiere conseguir q salida (en cada subtarea) con el par: (restaurante,mediaTotal de grasas)
    private static class ReduceClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private Text restaurante = new Text();
        private LongWritable mediaGrasasTot = new LongWritable();

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {
            //media de todas las medias de lo productos:
            long sum = 0L;
            long cont = 0L;
            for (LongWritable media3grasas : values) {
                sum += media3grasas.get();
                cont++;
            }

            mediaGrasasTot.set(sum / cont);
            context.write(key, mediaGrasasTot);

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

                    job.setNumReduceTasks(6);//6 subtareas equivalentes a los 6 restauantes

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
