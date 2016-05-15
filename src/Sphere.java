/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rollie
 */

public class Sphere extends AbsObject
{
    public double radius;
    public Vector objectPosition;

    @Override
    public Vector normal(Vector position)
    {
        return Vector.normal(Vector.subtract(position, objectPosition));
    }

    @Override
    public Intersect intersect(Ray _ray)
    {
        Vector centerOrigin = Vector.subtract(objectPosition, _ray.begin);

        double _vectorD = Vector.dotProduct(centerOrigin, _ray.direction);
        double _distance;
        double tempVal;

        if (_vectorD >= 0){
            tempVal = Math.pow(radius, 2) - (Vector.dotProduct(centerOrigin, centerOrigin) - Math.pow(_vectorD, 2));
            _distance = tempVal < 0 ? 0 : _vectorD - Math.sqrt(tempVal);
            
            if (_distance == 0){
                return null;
            }
            else{
                Sphere sphere = this;

                return new Intersect(){{
                    object = sphere;
                    ray = _ray;
                    distance = _distance;
                }};
            }
        }
        else{
            _distance = 0;
            
            return null;
        }
    }
}
