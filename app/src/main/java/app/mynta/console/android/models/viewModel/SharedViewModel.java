package app.mynta.console.android.models.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> requestCode = new MutableLiveData<>();
    private final MutableLiveData<String> packageName = new MutableLiveData<>();

    public void setRequestCode(Integer item) {
        requestCode.setValue(item);
    }

    public void setPackageName(String item) {
        packageName.setValue(item);
    }

    public LiveData<Integer> getRequestCode() {
        return requestCode;
    }

    public LiveData<String> getPackageName() { return packageName; }
}
