/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rollie
 */

public class Material{
    public java.util.function.Function<Vector, Colour> specular;
    public java.util.function.Function<Vector, Double> reflect;
    public java.util.function.Function<Vector, Colour> diffuse;
    public java.util.function.Function<Vector, Double> refractiveIndex;
    public double transparency;
    public double specularWidth;
    public String name;
}

