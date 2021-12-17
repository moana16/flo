package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpActivity :AppCompatActivity(),SignUpView {

    lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupSingupBtn.setOnClickListener {
            signUp()

        }

    }

    private fun getUser() : User {
        val email:String = binding.signupIdEt.text.toString()+"@"+binding.signupMailEt.text.toString()
        val pw : String=binding.signupPwEt.text.toString()
        val name : String = binding.signupNameEt.text.toString()

        return User(email,pw,name)
    }

//    private fun signUp() {
//        if(binding.signupIdEt.text.toString().isEmpty()||binding.signupMailEt.text.toString().isEmpty()) {
//            Toast.makeText(this,"이메일 형식이 잘못되었습니다",Toast.LENGTH_SHORT).show()
//            return
//        }
//        if(binding.signupPwEt.text.toString() != binding.signupPwCheckEt.text.toString()) {
//            Toast.makeText(this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()
//            return
//        }
//        val userDB = SongDB.getInstance(this)!!
//        userDB.UserDao().insert(getUser())
//
//        val users= userDB.UserDao().getUsers()
//
//
//        Toast.makeText(this,"해당 회원 정보가 존재하지 않습니다",Toast.LENGTH_SHORT).show()
//
//    }

    private fun signUp() {

        if(binding.signupIdEt.text.toString().isEmpty()||binding.signupMailEt.text.toString().isEmpty()) {
            Toast.makeText(this,"이메일 형식이 잘못되었습니다",Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.signupNameEt.text.toString().isEmpty()) {
            Toast.makeText(this,"이름 형식이 잘못되었습니다",Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.signupPwEt.text.toString() != binding.signupPwCheckEt.text.toString()) {
            Toast.makeText(this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()
            return
        }


        val authService = AuthService()
        authService.setSignUpView(this)
        authService.signUp(getUser())



        Log.d("SIGNUP/ASYNC","hello, ") //바로 넘어온 뒤 서버에서 응답이 오면
    }

    override fun onSignUpLoading() {
        binding.signupLoadingPb.visibility=View.VISIBLE

    }

    override fun onSignUpSuccess() {
        binding.signupLoadingPb.visibility=View.GONE
        finish()
    }

    override fun onSignUpFailure(code: Int, message: String) {
        binding.signupLoadingPb.visibility=View.GONE

        when(code) {
            2016,2017 -> {
                binding.signupEmailErrorTv.visibility=View.VISIBLE
                binding.signupEmailErrorTv.text=message

            }
        }
    }


}