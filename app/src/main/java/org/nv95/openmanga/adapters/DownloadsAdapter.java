/*
 * Copyright (C) 2016 Vasily Nikitin
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */

package org.nv95.openmanga.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.nv95.openmanga.R;
import org.nv95.openmanga.components.AsyncImageView;
import org.nv95.openmanga.providers.MangaInfo;
import org.nv95.openmanga.providers.MangaSummary;
import org.nv95.openmanga.providers.SaveService;

/**
 * Created by nv95 on 03.01.16.
 */
public class DownloadsAdapter extends BaseAdapter implements ServiceConnection {
    @Nullable
    private SaveService saveService;
    private LayoutInflater inflater;
    private final Intent intent;// = new Intent("org.nv95.openmanga.providers.SaveService");

    public DownloadsAdapter(Context context) {
        intent = new Intent(context, SaveService.class);
        saveService = null;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return saveService == null ? 0 : saveService.getQueue().size();
    }

    @Override
    public MangaInfo getItem(int position) {
        // TODO: 03.01.16
        return saveService == null ? null : saveService.getQueue().toArray(new MangaSummary[getCount()])[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_download, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.textView_title);
            viewHolder.textViewStatus = (TextView) convertView.findViewById(R.id.textView_status);
            viewHolder.asyncImageView = (AsyncImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MangaInfo info = getItem(position);
        viewHolder.textViewTitle.setText(info.getName());
        viewHolder.asyncImageView.setImageAsync(info.getPreview());
        return convertView;
    }

    public void enable() {
        inflater.getContext().bindService(intent, this, 0);
    }

    public void disable() {
        inflater.getContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        saveService = ((SaveService.SaveBinder) service).getService();
        notifyDataSetChanged();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        saveService = null;
        notifyDataSetInvalidated();
    }

    private static class ViewHolder {
        TextView textViewTitle;
        TextView textViewStatus;
        AsyncImageView asyncImageView;
    }
}
