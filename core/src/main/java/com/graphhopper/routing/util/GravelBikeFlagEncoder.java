/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.util;

import static com.graphhopper.routing.util.PriorityCode.*;

import java.util.TreeMap;

import com.graphhopper.reader.OSMRelation;
import com.graphhopper.reader.OSMWay;
import com.graphhopper.util.PMap;

/**
 * Specifies the settings for gravel bike
 * <p>
 * @author ratrun
 * @author Peter Karich
 * @author glandais
 */
public class GravelBikeFlagEncoder extends BikeCommonFlagEncoder
{
    public GravelBikeFlagEncoder()
    {
        this(4, 2, 0);
    }

    public GravelBikeFlagEncoder( PMap properties )
    {
        this(
                (int) properties.getLong("speedBits", 4),
                properties.getDouble("speedFactor", 2),
                properties.getBool("turnCosts", false) ? 1 : 0
        );
        this.properties = properties;
        this.setBlockFords(properties.getBool("blockFords", true));
    }

    public GravelBikeFlagEncoder( String propertiesStr )
    {
        this(new PMap(propertiesStr));
    }

    public GravelBikeFlagEncoder( int speedBits, double speedFactor, int maxTurnCosts )
    {
        super(speedBits, speedFactor, maxTurnCosts);
        setTrackTypeSpeed("grade1", 18); // paved
        setTrackTypeSpeed("grade2", 16); // now unpaved ...
        setTrackTypeSpeed("grade3", 12);
        setTrackTypeSpeed("grade4", 8);
        setTrackTypeSpeed("grade5", 6); // like sand/grass     

        setSurfaceSpeed("paved", 18);
        setSurfaceSpeed("asphalt", 18);
        setSurfaceSpeed("cobblestone", 10);
        setSurfaceSpeed("cobblestone:flattened", 10);
        setSurfaceSpeed("sett", 10);
        setSurfaceSpeed("concrete", 14);
        setSurfaceSpeed("concrete:lanes", 16);
        setSurfaceSpeed("concrete:plates", 16);
        setSurfaceSpeed("paving_stones", 16);
        setSurfaceSpeed("paving_stones:30", 16);
        setSurfaceSpeed("unpaved", 14);
        setSurfaceSpeed("compacted", 14);
        setSurfaceSpeed("dirt", 14);
        setSurfaceSpeed("earth", 14);
        setSurfaceSpeed("fine_gravel", 18);
        setSurfaceSpeed("grass", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("grass_paver", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("gravel", 14);
        setSurfaceSpeed("ground", 14);
        setSurfaceSpeed("ice", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("metal", 10);
        setSurfaceSpeed("mud", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("pebblestone", 12);
        setSurfaceSpeed("salt", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("sand", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("wood", PUSHING_SECTION_SPEED / 2);

        setHighwaySpeed("living_street", 12);
        setHighwaySpeed("steps", PUSHING_SECTION_SPEED / 2);

        setHighwaySpeed("road", 12);
        setHighwaySpeed("track", 18);
        setHighwaySpeed("path", 12);
        setHighwaySpeed("service", 14);
        setHighwaySpeed("residential", 16);
        setHighwaySpeed("unclassified", 16);
        setHighwaySpeed("cycleway", 18);
        setHighwaySpeed("footway", 12);
        setHighwaySpeed("pedestrian", 12);

        setHighwaySpeed("trunk", 18);
        setHighwaySpeed("trunk_link", 18);
        setHighwaySpeed("primary", 18);
        setHighwaySpeed("primary_link", 18);
        setHighwaySpeed("secondary", 18);
        setHighwaySpeed("secondary_link", 18);
        setHighwaySpeed("tertiary", 18);
        setHighwaySpeed("tertiary_link", 18);

        addPushingSection("steps");

        setCyclingNetworkPreference("icn", PREFER.getValue());
        setCyclingNetworkPreference("ncn", PREFER.getValue());
        setCyclingNetworkPreference("rcn", PREFER.getValue());
        setCyclingNetworkPreference("lcn", PREFER.getValue());
        setCyclingNetworkPreference("mtb", PREFER.getValue());

        avoidHighwayTags.add("trunk");
        avoidHighwayTags.add("trunk_link");
        avoidHighwayTags.add("primary");
        avoidHighwayTags.add("primary_link");
        avoidHighwayTags.add("secondary");
        avoidHighwayTags.add("secondary_link");
        avoidHighwayTags.add("tertiary");
        avoidHighwayTags.add("tertiary_link");

        preferHighwayTags.add("road");
        preferHighwayTags.add("track");
        preferHighwayTags.add("path");
        preferHighwayTags.add("service");
        preferHighwayTags.add("residential");
        preferHighwayTags.add("unclassified");
        preferHighwayTags.add("cycleway");
        preferHighwayTags.add("footway");
        preferHighwayTags.add("pedestrian");

        potentialBarriers.add("kissing_gate");
        setSpecificBicycleClass("touring");
    }

    @Override
    public int getVersion()
    {
        return 1;
    }

    @Override
    void collect( OSMWay way, TreeMap<Double, Integer> weightToPrioMap )
    {
        super.collect(way, weightToPrioMap);

        String highway = way.getTag("highway");
        if ("track".equals(highway))
        {
            String trackType = way.getTag("tracktype");
            if ("grade1".equals(trackType))
                weightToPrioMap.put(50d, VERY_NICE.getValue());
            else if ("grade2".equals(trackType))
                weightToPrioMap.put(50d, VERY_NICE.getValue());
            else if ("grade3".equals(trackType))
                weightToPrioMap.put(50d, VERY_NICE.getValue());
            else if ("grade4".equals(trackType))
                weightToPrioMap.put(50d, PREFER.getValue());
            else if ("grade5".equals(trackType))
                weightToPrioMap.put(50d, UNCHANGED.getValue());
            else if (trackType == null)
                weightToPrioMap.put(90d, PREFER.getValue());
        }
    }

    @Override
    public long handleRelationTags( OSMRelation relation, long oldRelationFlags )
    {
        oldRelationFlags = super.handleRelationTags(relation, oldRelationFlags);
        int code = 0;
        if (relation.hasTag("route", "bicycle"))
            code = PREFER.getValue();

        int oldCode = (int) relationCodeEncoder.getValue(oldRelationFlags);
        if (oldCode < code)
            return relationCodeEncoder.setValue(0, code);
        return oldRelationFlags;
    }

    @Override
    boolean allowedSacScale( String sacScale )
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "gravelbike";
    }
}
