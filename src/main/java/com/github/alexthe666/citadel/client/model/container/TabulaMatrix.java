package com.github.alexthe666.citadel.client.model.container;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.Stack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabulaMatrix {
   public Stack<Matrix4f> matrixStack = new Stack<>();

   public TabulaMatrix() {
      Matrix4f matrix = new Matrix4f();
      matrix.setIdentity();
      this.matrixStack.push(matrix);
   }

   public void push() {
      this.matrixStack.push(new Matrix4f(this.matrixStack.peek()));
   }

   public void pop() {
      if (this.matrixStack.size() < 2) {
         try {
            throw new Exception("Stack Underflow for tabula matrix!!!");
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      this.matrixStack.pop();
   }

   public void translate(float x, float y, float z) {
      Matrix4f matrix = this.matrixStack.peek();
      Matrix4f translation = new Matrix4f();
      translation.setIdentity();
      translation.setTranslation(x, y, z);
      matrix.multiply(translation);
   }

   public void translate(double x, double y, double z) {
      this.translate((float)x, (float)y, (float)z);
   }

   public void rotate(double angle, double x, double y, double z) {
      Matrix4f matrix = this.matrixStack.peek();
      Matrix4f rotation = new Matrix4f();
      rotation.setIdentity();
      rotation.load(new Matrix4f());
      matrix.multiply(rotation);
   }

   public void rotate(float angle, float x, float y, float z) {
      this.rotate(angle, x, y, z);
   }

   public void rotate(Matrix4f qaut) {
      Matrix4f matrix = this.matrixStack.peek();
      Matrix4f rotation = new Matrix4f();
      rotation.load(qaut);
      matrix.multiply(rotation);
   }

   public void scale(float x, float y, float z) {
      Matrix4f matrix = this.matrixStack.peek();
      Matrix4f scale = new Matrix4f();
      matrix.multiply(Matrix4f.createScaleMatrix(x, y, z));
   }

   public void scale(double x, double y, double z) {
      this.scale((float)x, (float)y, (float)z);
   }

   public void transform(Vector3f point) {
      Matrix4f matrix = this.matrixStack.peek();
      matrix.translate(point);
   }

   public Vector3f getTranslation() {
      Matrix4f matrix = this.matrixStack.peek();
      Vector3f translation = new Vector3f();
      matrix.translate(translation);
      return translation;
   }

   public Matrix4f getRotation() {
      Matrix4f matrix = this.matrixStack.peek();
      return matrix.copy();
   }

   public Vector3f getScale() {
      Matrix4f matrix = this.matrixStack.peek();
      return new Vector3f(1.0F, 1.0F, 1.0F);
   }

   public void multiply(TabulaMatrix matrix) {
      this.matrixStack.peek().multiply(matrix.matrixStack.peek());
   }

   public void multiply(Matrix4f matrix) {
      this.matrixStack.peek().multiply(matrix);
   }

   public void add(TabulaMatrix matrix) {
      this.matrixStack.peek().add(matrix.matrixStack.peek());
   }

   public void add(Matrix4f matrix) {
      this.matrixStack.peek().add(new Matrix4f(matrix));
   }

   public void invert() {
      this.matrixStack.peek().invert();
   }
}
