/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rollie
 */

public abstract class AbsObject {
    public Material material;
    public String name;
    
    public abstract Vector normal(Vector _vector);
    public abstract Intersect intersect(Ray ray);
}