package com.thesocialnetwork.linkr;

import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by corei3 on 12-05-2018.
 */

public class FilterUserList extends Filter {

    AdapterUserList adapter;
    ArrayList<ModelUserProfile> filterList;

    public FilterUserList(ArrayList<ModelUserProfile> filterList, AdapterUserList adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<ModelUserProfile> filteredPlayers=new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getProfile_name().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }
            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.listModel= (ArrayList<ModelUserProfile>) results.values;
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
