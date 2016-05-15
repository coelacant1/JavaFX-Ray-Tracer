/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;

/**
 *
 * @author Rollie
 */

abstract class View {
    public AbsObject[] objects;
    public Camera camera;
    public LightSource[] lightSource;
    
    public Iterable<Intersect> intersect(Ray ray){
        Iterable<Intersect> temp;
        Intersect[] tempL = new Intersect[objects.length];
        
        for (int i = 0; i < tempL.length; i++){
            tempL[i] = objects[i].intersect(ray);
        }
        
        temp = Arrays.asList(tempL);
        
        return temp;
    }
}
