package com.androidapps.sharelocation.SnapToRoadApiClient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


    public class SnapToRoadResponse
    {

        @SerializedName("snappedPoints")
        @Expose
        private List<SnappedPoint> snappedPoints = null;

        public SnapToRoadResponse() {
        }

        public List<SnappedPoint> getSnappedPoints() {
            return snappedPoints;
        }

        public void setSnappedPoints(List<SnappedPoint> snappedPoints) {
            this.snappedPoints = snappedPoints;
        }





    }
