/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.awt.image.BufferedImage;

/**
 * /EXTERNAL SOURCES/ located at lines 108 and 110
 * 
 * /TODO/:
 * Finish refraction
 *  ray splitting for reflection, and refraction
 *  total internal reflection
 *  Fresnel reflectivity
 * Add normal mapping
 * Add displacement mapping
 * Add Fresnel reflectivity
 * -Add super sampling, adaptive sampling, and stochastic sampling modes
 * Add Depth of Field
 * Add area light sources
 * Add diffuse inter-reflection
 * Add surface and object caustics
 * Add transparency
 * Add soft shadows
 * 
 * @author Rollie
 */

public class RayTracer {
    private final int bmpWidth;
    private final int bmpHeight;
    private final int amount = 4;
    
    public RayTracer(int w, int h){
        bmpWidth = w;
        bmpHeight = h;
    }

    private Iterable<Intersect> intersections(Ray ray, View view)
    {
        ArrayList<Intersect> rayIntersections = new ArrayList<>();
        
        for (AbsObject i : view.objects){
            Intersect temp = i.intersect(ray);
            if (temp != null){
                rayIntersections.add(temp);
            }
        }
        
        Collections.sort(rayIntersections, (Intersect x, Intersect y) -> Double.valueOf(x.distance).compareTo(y.distance));
        
        Iterable<Intersect> temp;
        Intersect[] tempL = new Intersect[rayIntersections.toArray().length];
        
        for (int i = 0; i < tempL.length; i++){
            tempL[i] = (Intersect)rayIntersections.toArray()[i];
        }
        
        temp = Arrays.asList(tempL);
        
        return temp;
    }
    
    private double shadowTest(Ray ray, View view)
    {
        Iterable<Intersect> intersections = intersections(ray, view);
        Intersect intersect = null;
        
        if(intersections.iterator().hasNext()){
            intersect = intersections.iterator().next();   
        }
        
        if (intersect == null){
            return 0;
        }
        else{
            return intersect.distance;
        }
    }
    
    private Colour trace(Ray ray, View view, int amount)
    {
        Iterable<Intersect> intersections = intersections(ray, view);
        Intersect intersect = null;
        
        if(intersections.iterator().hasNext()){
            intersect = intersections.iterator().next();   
        }
        
        if (intersect == null){
            return Colour.def;
        }
        else{
            return getShade(intersect, view, amount);
        }
    }
    
    private Colour getShade(Intersect intersect, View view, int _amount)
    {
        Vector direction = intersect.ray.direction;
        Vector position = Vector.add(Vector.multiply(intersect.distance, intersect.ray.direction), intersect.ray.begin);
        Vector normal = intersect.object.normal(position);
        //http://graphics.stanford.edu/courses/cs148-10-summer/docs/2006--degreve--reflection_refraction.pdf - SOURCE for reflectDirection vector + equation
        Vector reflectDirection = Vector.subtract(direction, Vector.multiply(2 * Vector.dotProduct(normal, direction), normal));
        //http://web.cse.ohio-state.edu/~hwshen/681/Site/Slides_files/reflection_refraction.pdf - SOURCE for refractDirection vector + equation
        //nr -> object refractive index
        //(nr(Norm . direction) - sqrt(1 - nr^2(1 - (normal . direction)^2)) * Normal) - normal*direction
        Vector refractDirection =
                Vector.subtract(
                        //Vector.multiply(intersect.object.material.refractiveIndex.apply(position), Vector.dotProduct(normal, direction)),
                        Vector.multiply(
                                (
                                    (intersect.object.material.refractiveIndex.apply(position) * Vector.dotProduct(normal, direction))- 
                                    Math.sqrt(1 - Math.pow(intersect.object.material.refractiveIndex.apply(position), 2) * (1 - Math.pow(Vector.dotProduct(normal, direction), 2)))
                                ),
                                normal
                            ),
                        Vector.multiply(intersect.object.material.refractiveIndex.apply(position), direction)
                );
        
        Colour outColor = Colour.def;
        
        outColor = Colour.add(outColor, getColour(intersect.object, position, normal, reflectDirection, view));
        //outColor = Colour.add(outColor, getColour(intersect.object, position, normal, refractDirection, view));
        //outColor = Colour.add(outColor, Colour.average(getColour(intersect.object, position, normal, reflectDirection, view), getColour(intersect.object, position, normal, refractDirection, view)));
        
        if (_amount < amount){
            Colour reflect = outColor;
            Colour refract = outColor;
            // Vector.add(position, Vector.multiply(0.001, refractDirection)) -> epsilon correction
            //return Colour.add(outColor, getReflectColor(intersect.object, Vector.add(position, Vector.multiply(0.001, reflectDirection)), reflectDirection, view, _amount));
            //return Colour.add(outColor, getRefractColor(intersect.object, Vector.add(position, Vector.multiply(0.001, refractDirection)), refractDirection, view, _amount));
            
            if (intersect.object.material.refractiveIndex.apply(normal) < 1){
                //Vector refractDir = Vector.normal(refract());
                //refract = getRefractColor(intersect.object, Vector.add(normal, Vector.multiply(0.001, refractDirection)), refractDirection, view, _amount);
            }
            
            reflect = getReflectColor(intersect.object, Vector.add(position, Vector.multiply(0.001, reflectDirection)), reflectDirection, view, _amount);
            refract = getRefractColor(intersect.object, Vector.add(normal, Vector.multiply(0.001, refractDirection)), refractDirection, view, _amount);

            reflect = Colour.multiply((1 - intersect.object.material.transparency), reflect);
            refract = Colour.multiply((intersect.object.material.transparency), refract);
            
            return Colour.add(outColor, Colour.add(reflect, refract));
        }
        else
        {
            return outColor;
        }
    }
    
    private Colour getReflectColor(AbsObject object, Vector position, Vector rayDirection, View view, int _amount)
    {
        return Colour.multiply(object.material.reflect.apply(position), trace(
                new Ray() {{ 
                    begin = position;
                    direction = rayDirection;
                }},
                view,
                _amount + 1
        ));
    }
    
    //NOT FINISHED
    private Colour getRefractColor(AbsObject object, Vector normal, Vector rayDirection, View view, int _amount){
        //double tempFresnel = fresnel(rayDirection, normal, object, 1.5);
        
        return Colour.multiply(object.material.refractiveIndex.apply(normal), trace(
                new Ray() {{ 
                    begin = normal;
                    direction = rayDirection;
                }},
                view,
                _amount + 1
        ));
    }
    
    private double fresnel(Vector direction, Vector normal, AbsObject object, double indexRefraction){
        double cosineDirection = Vector.dotProduct(direction, normal);
        double etaDirection = 1;
        double etaT = object.material.refractiveIndex.apply(normal);
        
        if (cosineDirection > 1){
            cosineDirection = 1;
        }
        else if (cosineDirection < -1){
            cosineDirection = -1;
        }
        
        if(cosineDirection > 0){
            etaDirection = etaT;
            etaT = 1;
        }
        
        double sinT = (etaDirection / etaT) * Math.sqrt(Math.max(0.d, 1 - Math.pow(cosineDirection, 2)));
        
        if (sinT >= 1){
            indexRefraction = 1;
        }
        else{
            double cosT = Math.sqrt(Math.max(0.d, 1 - Math.pow(sinT, 2)));
            cosineDirection = Math.abs(cosineDirection);
            
            double rS = ((etaT * cosineDirection) - (etaDirection * cosT)) / ((etaT * cosineDirection) + (etaDirection * cosT));
            double rP = ((etaDirection * cosineDirection) - (etaT * cosT)) / ((etaDirection * cosineDirection) + (etaT * cosT));
            indexRefraction = (Math.pow(rS, 2) + Math.pow(rP, 2)) / 2;
        }
        
        return indexRefraction;
    }

    private Colour getColour(AbsObject object, Vector position, Vector normal, Vector rayDirection, View view)
    {
        Colour outColor = Colour.create(0, 0, 0);
        
        for (LightSource light : view.lightSource)
        {
            Vector lightDistance = Vector.subtract(light.position, position);
            Vector normalLD = Vector.normal(lightDistance);
            
            double cleanIntersection = shadowTest(
                    new Ray() {{
                        begin = position;
                        direction = normalLD;
                    }}
                , view);
            
            boolean notInShadow = ((cleanIntersection > Vector.magn(lightDistance)) || (cleanIntersection == 0));
            
            if (notInShadow)
            {
                double illumination = Vector.dotProduct(normalLD, normal);
                Colour lightColor = illumination > 0 ? Colour.multiply(illumination, light.color) : Colour.create(0, 0, 0);
                double specular = Vector.dotProduct(normalLD, Vector.normal(rayDirection));
                Colour specularColor = specular > 0 ? Colour.multiply(Math.pow(specular, object.material.specularWidth), light.color) : Colour.create(0, 0, 0);
                outColor = Colour.add(outColor, Colour.add(Colour.multiply(object.material.diffuse.apply(position), lightColor), Colour.multiply(object.material.specular.apply(position), specularColor)));
            }
        }
        
        return outColor;
    }

    private Vector getLocation(double x, double y, Camera camera)
    {
        return Vector.normal(Vector.add(camera.front, Vector.add(Vector.multiply(centerHorizontal(x), camera.right), Vector.multiply(centerVertical(y), camera.above))));
    }
    
    private double centerHorizontal(double horizontal)
    {
        double temp;
        
        temp = (horizontal - (bmpWidth / 2.0)) / (2.0 * bmpWidth);
        
        return temp;
    }
    
    private double centerVertical(double vertical)
    {
        double temp;
        
        temp = -(vertical - (bmpHeight / 2.0)) / (2.0 * bmpHeight);
        
        return temp;
    }

    BufferedImage render(View view)
    {
        final BufferedImage image = new BufferedImage( bmpWidth, bmpHeight, BufferedImage.TYPE_INT_RGB );
        
        for (int y = 0; y < bmpHeight; y++)
        {
            for (int x = 0; x < bmpWidth; x++)
            {
                Vector _direction = getLocation(x, y, view.camera);
                
                Colour colour = trace(
                        new Ray() {{
                            begin = view.camera.position;
                            direction = _direction;
                        }},
                        view,
                        0);
                
               image.setRGB(x, y, colour.toColour().getRGB());
            }
        }
        
        return image;
    }
    
    BufferedImage renderPreview(View view, int quality){
        final BufferedImage image = new BufferedImage( bmpWidth, bmpHeight, BufferedImage.TYPE_INT_RGB );
        int incrementVal = bmpWidth / quality;
        
        for (int y = 0; y < bmpHeight; y += bmpHeight / incrementVal)
        {
            for (int x = 0; x < bmpWidth; x += bmpHeight / incrementVal)
            {
                Vector _direction = getLocation(x, y, view.camera);
                
                Colour colour = trace(
                        new Ray() {{
                            begin = view.camera.position;
                            direction = _direction;
                        }},
                        view,
                        0);
                       
                for (int yM = 0; yM < bmpHeight / incrementVal; yM++)
                {
                    for(int xM = 0; xM < bmpWidth / incrementVal; xM++)
                    {
                        image.setRGB(xM + x, yM + y, colour.toColour().getRGB());
                    }
                }
            }
            
            rayTraceRender.setImage(image);
        }
        
        return image;
    }
    
    BufferedImage renderSuperSample(View view, int depth, double filterSize)
    {
        final BufferedImage image = new BufferedImage( bmpWidth, bmpHeight, BufferedImage.TYPE_INT_RGB );
        double increment = filterSize / (double)depth;
        
        for (int y = 0; y < bmpHeight; y++)
        {
            for (int x = 0; x < bmpWidth; x++)
            {
                ArrayList<Vector> tempVectors = new ArrayList<>();
                Vector[] vectors = null;
                
                for (double yV = -(filterSize / 2); yV <= (filterSize / 2); yV += increment){
                    for (double xV = -(filterSize / 2); xV <= (filterSize / 2); xV += increment){
                        tempVectors.add(getLocation(x + xV, y + yV, view.camera));
                    }
                }
                
                vectors = tempVectors.toArray(new Vector[0]);
                
                Colour colour = null;
                
                for (int i = 0; i < vectors.length; i++){
                    Vector tempVector = vectors[i];
                    
                    if (i == 0){
                        colour = trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 
                            0
                        );
                    }
                    else{
                        colour = Colour.average(colour, trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 0
                        ));
                    }
                }
                
                image.setRGB(x, y, colour.toColour().getRGB());
            }
            
            rayTraceRender.setImage(image);
        }
        
        return image;
    }
    
    BufferedImage renderAdaptiveSample(View view, int maxDepth, double filterSize)
    {
        final BufferedImage image = new BufferedImage( bmpWidth, bmpHeight, BufferedImage.TYPE_INT_RGB );
        
        for (int y = 0; y < bmpHeight; y++)
        {
            for (int x = 0; x < bmpWidth; x++)
            {
                int iterator = 1;
                double increment = filterSize;
                Colour tempAvgColour = null;
                Vector[] vectors = null;
                
                while(iterator <= maxDepth){
                    ArrayList<Vector> tempVectors = new ArrayList<>();
                    
                    for (double yV = -(filterSize / 2); yV <= (filterSize / 2); yV += increment){
                        for (double xV = -(filterSize / 2); xV <= (filterSize / 2); xV += increment){
                            tempVectors.add(getLocation(x + xV, y + yV, view.camera));
                        }
                    }
                    
                    vectors = tempVectors.toArray(new Vector[0]);
                    
                    Colour[] colourArr = new Colour[vectors.length];
                    
                    for (int i = 0; i < vectors.length; i++){
                        Vector tempVector = vectors[i];
                        colourArr[i] = trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 
                            0
                        );
                    }
                    
                    double averageChange = 0.0;
                    
                    boolean wasSet = false;
                    
                    for(int i = 0; i < colourArr.length; i++){
                        for (int j = 0; i < colourArr.length; i++){
                            if(!wasSet){
                                averageChange = (((double)colourArr[i].R + (double)colourArr[i].G + (double)colourArr[i].B) / 3.0) - ((double)colourArr[j].R + (double)colourArr[j].G + (double)colourArr[j].B) / 3.0;
                                wasSet = true;
                            }
                            else if(i != j){
                                averageChange = (((((double)colourArr[i].R + (double)colourArr[i].G + (double)colourArr[i].B) / 3.0) - ((double)colourArr[j].R + (double)colourArr[j].G + (double)colourArr[j].B) / 3.0) + averageChange) / 2.0;
                            }
                        }
                    }
                    
                    if (averageChange < 0.1 && averageChange > -0.1){
                        break;
                    }
                    else{
                        iterator *= 2;
                        increment /= iterator;
                    }
                }
                
                for (int i = 0; i < vectors.length; i++){
                    Vector tempVector = vectors[i];

                    if (i == 0){
                        tempAvgColour = trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 
                            0
                        );
                    }
                    else{
                        tempAvgColour = Colour.average(tempAvgColour, trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 0
                        ));
                    }
                }
                
                image.setRGB(x, y, tempAvgColour.toColour().getRGB());
            }
            
            rayTraceRender.setImage(image);
        }
        
        return image;
    }
    
    BufferedImage renderStochasticSample(View view, int depth, double filterSize)
    {
        final BufferedImage image = new BufferedImage( bmpWidth, bmpHeight, BufferedImage.TYPE_INT_RGB );
        
        double increment = filterSize / (double)depth;
        
        for (int y = 0; y < bmpHeight; y++)
        {
            for (int x = 0; x < bmpWidth; x++)
            {
                ArrayList<Vector> tempVectors = new ArrayList<>();
                Vector[] vectors = null;
                
                for (double yV = -(filterSize / 2); yV <= (filterSize / 2); yV += increment){
                    for (double xV = -(filterSize / 2); xV <= (filterSize / 2); xV += increment){
                        double randX = Math.random() * increment * (Math.random() < 0.5 ? -1 : 1) + xV;
                        double randY = Math.random() * increment * (Math.random() < 0.5 ? -1 : 1) + yV;
                        
                        tempVectors.add(getLocation(x + randX, y + randY, view.camera));
                    }
                }
                
                vectors = tempVectors.toArray(new Vector[0]);
                
                Colour colour = null;
                
                for (int i = 0; i < vectors.length; i++){
                    Vector tempVector = vectors[i];
                    
                    if (i == 0){
                        colour = trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 
                            0
                        );
                    }
                    else{
                        colour = Colour.average(colour, trace(
                            new Ray() {{ 
                                begin = view.camera.position; 
                                direction = tempVector;
                            }}, 
                            view, 0
                        ));
                    }
                }
                
                image.setRGB(x, y, colour.toColour().getRGB());
            }
            
            rayTraceRender.setImage(image);
        }
        
        return image;
    }
}