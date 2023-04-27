package controller;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import model.Task;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class CPMGraphGenerator {

    public void generate(ArrayList<Task> tasks, String criticalPath, float mult) {
        // Create a new directed graph
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        // Set the style of the nodes
        graph.getModel().beginUpdate();
        try {
            graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
            graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
            graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_STROKECOLOR, "#000000");
            graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");
        } finally {
            graph.getModel().endUpdate();
        }

        int numVert = tasks.size();
        //       System.out.println("vert:" + numVert);
        // Define the nodes and their positions
        ArrayList<Object> vert = new ArrayList<>();

        for (int i = 0; i < numVert; i++) {
            vert.add(graph.insertVertex(parent, null,  i + "  " + i+1 + "\njjhjh", 0, 0, 80, 30));
        }
/*
// do wywalenia
        // Define the edges
        graph.insertEdge(parent, null, "2", n1, n2);
        graph.insertEdge(parent, null, "3", n1, n3);
        graph.insertEdge(parent, null, "4", n2, n4);
        graph.insertEdge(parent, null, "5", n3, n5);
        graph.insertEdge(parent, null, "6", n4, n5);
*/
        System.out.println("critical:" + criticalPath);
        List<String> criticalVert = Arrays.asList(criticalPath.split(" "));
        // tylko debug
//        for (var elem : criticalVert) {
//            System.out.println(elem);
//        }

        for (int i = 1; i < tasks.size(); i++) { // -1 bo ostatnia pozorna ......{
            for (int j = 0; j < tasks.get(i).getDependencies().size(); j++) {
                System.out.println(i-1);
                var e = graph.insertEdge(parent, null, tasks.get(tasks.get(i).getDependency(j)).getName() + " " + tasks.get(tasks.get(i).getDependency(j)).getTime(), vert.get(tasks.get(i).getDependency(j)), vert.get(i));
                if (criticalVert.contains(tasks.get(tasks.get(i).getDependency(j)).getName())) {
                    e = graph.insertEdge(parent, null, tasks.get(tasks.get(i).getDependency(j)).getName() + " " + tasks.get(tasks.get(i).getDependency(j)).getTime(), vert.get(tasks.get(i).getDependency(j)), vert.get(i),
                            "strokeWidth=3;strokeColor=grey;"
                    );
                }
                // tu probowalismy zmienic kolor labela na grafie
//                var edgeState = graph.getView().getState(e);
//                var labelState = edgeState.getLabel();
                //var labelCellId = e.

            }
        }
        // Calculate the layout of the graph
        mxIGraphLayout layout = new mxCircleLayout(graph);
        layout.execute(parent);


        // Save the graph as an image
        float scale = 2.55f + mult;
        System.out.println("skala: " + scale);
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, scale, Color.WHITE, true, null);

//         wszystko tu jest do wywalenia bo nie dziala
//        BufferedImage before = image;
//        int w = before.getWidth();
//        int h = before.getHeight();
//        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//        AffineTransform at = new AffineTransform();
//        at.scale(2.0, 2.0);
//        AffineTransformOp scaleOp =
//                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//        image = scaleOp.filter(before, after);

        File file = new File("img\\cpm-graph.png");
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}