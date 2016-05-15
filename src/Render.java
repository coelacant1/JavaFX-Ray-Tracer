/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 *
 * @author Rollie
 */ 

public class Render {
    public static Image render(int x, int y, View view){
        RayTracer rayTracer = new RayTracer(x, y);
        BufferedImage temp = rayTracer.render(view);
        Image image = SwingFXUtils.toFXImage(temp, null);
        
        //System.out.println("Standard render complete.");
        
        return image;
    }
    
    public static Image renderPreview(int x, int y, View view, int accuracy){
        RayTracer rayTracer = new RayTracer(x, y);
        BufferedImage temp = rayTracer.renderPreview(view, accuracy);
        Image image = SwingFXUtils.toFXImage(temp, null);
        
        //System.out.println("Render preview complete.");
        
        return image;
    }
    
    public static Image renderSuperSample(int x, int y, View view, int depth, double filterSize){
        RayTracer rayTracer = new RayTracer(x, y);
        BufferedImage temp = rayTracer.renderSuperSample(view, depth, filterSize);
        Image image = SwingFXUtils.toFXImage(temp, null);
        
        //System.out.println("Super sampled render complete.");
        
        return image;
    }
    
    public static Image renderAdaptiveSample(int x, int y, View view, int depth, double filterSize){
        RayTracer rayTracer = new RayTracer(x, y);
        BufferedImage temp = rayTracer.renderAdaptiveSample(view, depth, filterSize);
        Image image = SwingFXUtils.toFXImage(temp, null);
        
        //System.out.println("Adaptive sampled render complete.");
        
        return image;
    }
    
    public static Image renderStochasticSample(int x, int y, View view, int depth, double filterSize){
        RayTracer rayTracer = new RayTracer(x, y);
        BufferedImage temp = rayTracer.renderStochasticSample(view, depth, filterSize);
        Image image = SwingFXUtils.toFXImage(temp, null);
        
        //System.out.println("Stochastic sampled render complete.");
        
        return image;
    }
}
