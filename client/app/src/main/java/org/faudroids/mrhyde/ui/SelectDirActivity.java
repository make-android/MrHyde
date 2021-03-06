package org.faudroids.mrhyde.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;

import org.faudroids.mrhyde.R;
import org.faudroids.mrhyde.app.MrHydeApp;

import java.io.File;

import butterknife.BindView;

public final class SelectDirActivity extends AbstractDirActivity {

  static final String EXTRA_IGNORE_DIR = "EXTRA_IGNORE_DIR"; // passed into this activity, optional
  static final String EXTRA_SELECTED_DIR = "EXTRA_SELECTED_DIR";  // part of result of this activity

  @BindView(R.id.back) protected View backView;
  @BindView(R.id.cancel) protected View cancelView;
  @BindView(R.id.confirm) protected View confirmView;

  private Optional<File> dirToIgnore;


  @Override
  public void onCreate(final Bundle savedInstanceState) {
    ((MrHydeApp) getApplication()).getComponent().inject(this);
    setContentView(R.layout.dialog_select_dir);
    super.onCreate(savedInstanceState);

    // hide action bar
    if (getSupportActionBar() != null) getSupportActionBar().hide();

    // setup buttons
    backView.setOnClickListener(v -> finish());
    cancelView.setOnClickListener(v -> finish());
    confirmView.setOnClickListener(v -> {
      // return result
      Intent resultIntent = new Intent(getIntent());
      resultIntent.putExtra(EXTRA_SELECTED_DIR, fileAdapter.getSelectedDir());
      setResult(RESULT_OK, resultIntent);
      finish();
    });

    // get args
    dirToIgnore = Optional.fromNullable((File) getIntent().getSerializableExtra(EXTRA_IGNORE_DIR));
  }


  @Override
  protected FileAdapter createAdapter() {
    return new AlphaFileAdapter(gitManager.getRepository().getRootDir());
  }


  @Override
  protected void onDirSelected(File directory) {
    // nothing to do
  }


  @Override
  protected void onFileSelected(File file) {
    // nothing to do
  }


  public class AlphaFileAdapter extends FileAdapter {

    public AlphaFileAdapter(File rootDir) {
      super(rootDir);
    }

    @Override
    public AlphaFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
      return new AlphaFileViewHolder(view);
    }


    public class AlphaFileViewHolder extends FileViewHolder {

      public AlphaFileViewHolder(View view) {
        super(view);
      }

      @Override
      public void setFile(final File file) {
        super.setFile(file);

        boolean isIgnoredDir = dirToIgnore.isPresent() && dirToIgnore.get().equals(file);

        // remove left + right margin due to dialog container
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        view.setLayoutParams(params);

        // reduce alpha for files
        float alpha = (file.isDirectory() && !isIgnoredDir) ? 1f : 0.3f;
        iconView.setAlpha(alpha);
        titleView.setAlpha(alpha);

        view.setOnClickListener(view1 -> {
          if (!file.isDirectory() || isIgnoredDir) {
            return;
          }

          // navigate "down"
          fileAdapter.setSelectedDir(file);
        });
      }
    }
  }

}
