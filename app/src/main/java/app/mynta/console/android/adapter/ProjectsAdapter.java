package app.mynta.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnProjectClickListener;
import app.mynta.console.android.models.Projects;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Projects> projectsList;
    private final OnProjectClickListener listener;

    public ProjectsAdapter(List<Projects> projectsList, OnProjectClickListener listener) {
        this.projectsList = projectsList;
        this.listener = listener;
    }

    static class ProjectsHolder extends RecyclerView.ViewHolder {

        TextView name, packagename;

        ProjectsHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.project_name);
            packagename = itemView.findViewById(R.id.project_packagename);
        }

        public void setData(Projects data) {
            name.setText(data.getName());
            packagename.setText(data.getPackagename());
        }

        void bind(final Projects item, final OnProjectClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_listitem, parent, false);
        return new ProjectsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Projects projects = projectsList.get(position);
        ProjectsHolder projectsHolder = (ProjectsHolder) holder;
        projectsHolder.setData(projects);
        projectsHolder.bind(projectsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

}
