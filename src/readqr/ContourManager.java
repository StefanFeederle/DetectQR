package readqr;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

public class ContourManager {
	private List<List<MatOfPoint>> markers = new ArrayList<List<MatOfPoint>>();
	private List<MatOfPoint> tmpContours = new ArrayList<MatOfPoint>();
	public List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	public Mat hierarchy = new Mat();
	public int hierarchyChannels;
	
	public ContourManager(List<MatOfPoint> contours, Mat hierarchy){
		this.contours = contours;
		this.hierarchy = hierarchy;
		hierarchyChannels = this.hierarchy.channels();
	}
	
	private int getFirstChildID(int contourID){
		int hierarchySlice[] = new int[ (int) (hierarchyChannels)];
		hierarchy.get(0, contourID, hierarchySlice);		
		return hierarchySlice[2];
	}
	
	private void findAndAddChildren(int parentID){
		//recursive finding of children and adding to tmpContours
		int firstChildID = getFirstChildID(parentID);
		if(firstChildID != -1){
			this.tmpContours.add(this.contours.get(parentID));
			this.findAndAddChildren(firstChildID);
		}
	}
	
	public List<List<MatOfPoint>> findNestedContours(int nestingDepth){
		markers.clear();
		for ( int i = 0; i < this.contours.size(); i++ ){
			this.findAndAddChildren(i);
			//System.out.println(this.tmpContours.size());
			if(this.tmpContours.size() == nestingDepth)
			this.markers.add(new ArrayList<MatOfPoint>(this.tmpContours));
			this.tmpContours.clear();
		}
		return markers;
	}
	
	
	
}
