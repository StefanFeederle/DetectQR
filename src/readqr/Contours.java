package readqr;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

public class Contours extends MatOfPoint {
	ContourTree tree = new ContourTree();
	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	
	public Contours(Mat canny_image){
		Mat hierarchy = new Mat();
		Imgproc.findContours( canny_image, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	}
	
	public ContourTree getTree(){
		return this.tree;
	}

}
