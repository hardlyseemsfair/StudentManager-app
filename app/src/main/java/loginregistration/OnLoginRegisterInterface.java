package loginregistration;

public interface OnLoginRegisterInterface {

    public void startApplication();
    public void setRegisterFragment();
    public void setLoginFragment();

    public void onLoginComplete(int response);

    public void onRegisterComplete(int response);



}
