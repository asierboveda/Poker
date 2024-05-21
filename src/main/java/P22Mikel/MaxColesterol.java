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
    
    //Mapeo --> reducir a (Restaurante, nombreProducto+ColesterolProducto) 
    //se tendrán tantas salidas como productos VÁLIDOS presente el restaurante seleccionado
    //VÁLIDO = registro con longitud>7, colesterol con Valor que NO sea vacío ni un espacio en blanco
    public static class MapClass extends Mapper<LongWritable, Text, Text, Text> {

        private Text restaurante = new Text();
        private Text productoColesterol = new Text();
        private String userRestaurante;
        //se registra en la variable userRestaurante, el valor enviado desde el MAIN
        @Override
        protected void setup(Context context) {
            userRestaurante = context.getConfiguration().get("restaurante");
            System.out.println("aqui: " + userRestaurante);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                //se divide en campos, teniendo en cuenta los valores que ya presentan comas de por sí
                String[] linea = value.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                
                if (linea.length > 7) {
                    String restauranteStr = linea[0];
                    //se restringe a los registros que sean del restaurante seleccionado
                    if (!restauranteStr.startsWith(userRestaurante)) {
                        return;
                    }
                    restaurante.set(restauranteStr);
                    
                    //construir el valor de la salida:
                    
                    //quitar comillas en los valores de colesterol: "47" y las comas en los productos que tienen comas de por sí
                    String productoStr = linea[1].replaceAll("[,\"]", "").trim();
                    String colesterolStr = linea[7].replaceAll("[,\"]", "").trim();

                    if (!colesterolStr.isEmpty() && !colesterolStr.equals("Cholesterol") && !colesterolStr.equals(" ")) { //evitar valores vacíos y la cabecera
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
    
    //Reductor --> conseguimos como salida 1 único registro: (Restaurante, producto+Colesterol)
    public static class ReduceClass extends Reducer<Text, Text, Text, Text> {
        
        private Text result = new Text();
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String maxProducto = "";
            int maxColesterol = Integer.MIN_VALUE;//comparador inicial
            //se analiza el colesterol de cada producto
            for (Text val : values) {
                
                String[] productoColesterol = val.toString().split(",", -1);
                String producto = productoColesterol[0].replaceAll("[,\"]", "").trim();
                String colesterolStr = productoColesterol[1].replaceAll("[,\"]", "").trim();
                System.out.println("productoColesterol=" + producto + "," + colesterolStr);//sacar por pantalla cada valor de los pares clave-valor del restaurante
                
                //limpiar la clave conseguida
                if (colesterolStr.contains("<")) {
                    colesterolStr = colesterolStr.replace("<", "");
                }
                if (colesterolStr.contains(" ")) {
                    colesterolStr = colesterolStr.replace(" ", "");
                }
                
                //Fase de conseguir el producto con mayor colesterol
                try {
                    //evitar valores nulos
                    if (!colesterolStr.isEmpty() && !colesterolStr.startsWith(" ") && !colesterolStr.equals("")) {
                        int colesterol = Integer.parseInt(colesterolStr);
                        if (colesterol > maxColesterol) {
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
        //antes del main, guardamosn el valor del restaurante elegido
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el nombre del restaurante (McDonald, Burger, Wendy, KFC, Taco, Pizza Hut):");
        String restaurante = scanner.nextLine();

        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("a_83036");
        try {
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://192.168.10.1:9000");
                    conf.set("restaurante", restaurante);//se guarda
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
