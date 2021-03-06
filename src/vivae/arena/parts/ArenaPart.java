/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package vivae.arena.parts;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * @author Petr Smejkal
 */
public abstract class ArenaPart {
    
    protected Shape shape;
    protected boolean isAntialiased = false;
    protected float x,y;
    
    ArenaPart(float x,float y)
    {
            this.x = x;
            this.y = y;
    }
    

    public abstract void paintComponent(Graphics g);

    public Shape getShape() {
            return shape;
    }

    public Area getArea(){
            Area a = null;
            if(getShape() != null) {
            a = new Area(getShape());
                AffineTransform o = AffineTransform.getTranslateInstance(x, y);
                a = a.createTransformedArea(o);
            }
            return a;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString(){
        return "ArenaPart at " + "[" + getX() + ", " + getY() + "]";
    }

    public boolean isAntialiased() {
        return isAntialiased;
    }

    public void setAntialiased(boolean isAntialiased) {
        this.isAntialiased = isAntialiased;
    }
}