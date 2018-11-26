import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;


public final class ProcessFile extends SimpleFileVisitor<Path> {

    private String currentFileContent = null;
    private String currentFilePath = null;
    private Graph graph;
    private String stopWords = "\\b( ? '|could|always|every|ever|think|nothin|shall|say|see|great|good|soon|though|said|one|know|little|never|well|without|would|must|much|might|may|ourselves|hers|between|yourself|but|again|there|about|once|during|out|very|having|with|they|own|an|be|some|for|do|its|yours|such|into|of|most|itself|other|off|is|s|am|or|who|as|from|him|each|the|themselves|until|below|are|we|these|your|his|through|don|nor|me|were|her|more|himself|this|down|should|our|their|while|above|both|up|to|ours|had|she|all|no|when|at|any|before|them|same|and|been|have|in|will|on|does|yourselves|then|that|because|what|over|why|so|can|did|not|now|under|he|you|herself|has|just|where|too|only|myself|which|those|i|after|few|whom|t|being|if|theirs|my|against|a|by|doing|it|how|further|was|here|than)\\b";
    public ProcessFile(Graph graph){
        this.graph = graph;
    }


    private void readFileContent(String file){
        currentFilePath = file;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            currentFileContent = sb.toString();
            currentFileContent.replaceAll("Mr.","Mr");
            String[] sentences = currentFileContent.split("[.]");
            for(String s:sentences){
                s = s.replaceAll(System.getProperty("line.separator"), " ");
                s = s.toLowerCase();
                s = s.replaceAll(stopWords,"");
                s = s.replaceAll("(-|;|,|!|”|“|)","");
                String[] words = s.split(" ");
                int pastVertex = -1;
                for (String w: words){
                    if(!w.equals("") && !w.equals(" ")) {
                        Vertex v = new Vertex(w);
                        int vId = graph.addVertex(v);
                        if (pastVertex != -1) {
                            graph.addNeighbor(pastVertex,vId,1);
                        }
                        pastVertex = vId;
                    }
                }
            }
        } catch (IOException e){
            System.out.println("readFileContent - " + e);
        }
    }

    @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        readFileContent(file.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}