package com.example.flo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
   private lateinit var signUpView : SignUpView
    private lateinit var loginView : LoginView

    fun setSignUpView(signUpView : SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView : LoginView) {
        this.loginView = loginView
    }

    fun signUp(user: User) {

        val authService=getRetrofit().create(AuthRetrofitInterface::class.java)

        signUpView.onSignUpLoading()

        authService.signUp(user).enqueue(object : Callback<AuthResponse> { //API 호출
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) { //두개 중 한 메소드가 호출될것임
                Log.d("SIGNUP/API-RESPONSE",response.toString())

                val resp = response.body()

                when(resp?.code) {
                    1000->signUpView.onSignUpSuccess()
                    else-> signUpView.onSignUpFailure(resp!!.code,resp.message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("SIGNUP/API-ERROR",t.toString())

                signUpView.onSignUpFailure(400,"네트워크 오류가 발생하였습니다")
            }

        })

        Log.d("SIGNUP/ASYNC","hello, ") //바로 넘어온 뒤 서버에서 응답이 오면
    }

    fun login(user: User) {

        val authService=getRetrofit().create(AuthRetrofitInterface::class.java)

        loginView.onLoginLoading()

        authService.login(user).enqueue(object : Callback<AuthResponse> { //API 호출
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) { //두개 중 한 메소드가 호출될것임
                Log.d("LOGIN/API-RESPONSE",response.toString())

                val resp = response.body()

                when(resp?.code) {
                    1000->loginView.onLoginSuccess(resp.result!!)
                    else-> loginView.onLoginFailure(resp!!.code,resp.message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("LOGIN/API-ERROR",t.toString())

                loginView.onLoginFailure(400,"네트워크 오류가 발생하였습니다")
            }

        })

        Log.d("LOGIN/ASYNC","hello, ") //바로 넘어온 뒤 서버에서 응답이 오면
    }



}