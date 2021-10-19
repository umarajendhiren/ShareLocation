package com.androidapps.sharelocation.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.BR;
import com.androidapps.sharelocation.OnClickCallListener;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.MyViewHolder> {
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
        return new MyViewHolder(binding);
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        ViewModel viewModel = getViewmodel();
     // DriverListBottomSheetAdapter.OnPhoneCallClickInterface  onPhoneCallClickInterface=getOnPhoneCallInterface();
        OnClickCallListener onClickCallListener=getOnCallListener();
        holder.bind(obj, viewModel,onClickCallListener,position);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj, ViewModel viewModel,OnClickCallListener callListener,int ObjectPosition) {
            binding.setVariable(BR.viewModel, viewModel );
            binding.setVariable(BR.pojo, obj);

            binding.setVariable(BR.callListener,callListener);
            binding.setVariable(BR.Objectposition,ObjectPosition);

           // binding.setVariable(BR.)
            binding.executePendingBindings();
        }
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    protected abstract ViewModel getViewmodel();
    protected abstract OnClickCallListener getOnCallListener();

/*protected  abstract  DriverListBottomSheetAdapter.OnPhoneCallClickInterface getOnPhoneCallInterface();*/



}