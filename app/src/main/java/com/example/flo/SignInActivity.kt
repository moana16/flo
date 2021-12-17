package com.example.flo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySigninBinding

class SignInActivity :AppCompatActivity() ,LoginView{
    lateinit var binding : ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinSignupBtn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.signinSinginBtn.setOnClickListener {
            login()

        }
    }

//    private fun login() {
//        if (binding.signinIdEt.text.toString().isEmpty() || binding.signinMailEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (binding.signinPwEt.text.isEmpty()) {
//            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val email:String = binding.signinIdEt.text.toString()+"@"+binding.signinMailEt.text.toString()
//        val pw : String=binding.signinPwEt.text.toString()
//
//        val userDB = SongDB.getInstance(this)!!
//        val user = userDB.UserDao().getUser(email,pw)
//
//        if(user==null) {
//            Toast.makeText(this,"없는 회원정보 입니다",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        user?.let {
//            Log.d("LOGIN","userId : ${user.id}, $user")
//            saveJwt(user.id)
//            startMainActivity()
//        }
//
//    }

    private fun login() {
        if (binding.signinIdEt.text.toString().isEmpty() || binding.signinMailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.signinPwEt.text.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val email:String = binding.signinIdEt.text.toString()+"@"+binding.signinMailEt.text.toString()
        val pw : String=binding.signinPwEt.text.toString()

        val authService=AuthService()
        authService.setLoginView(this)
        val user=User(email,pw,"")

        authService.login(user)
    }

    private fun startMainActivity() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }


    override fun onLoginLoading() {
        binding.signinLoadingPb.visibility= View.VISIBLE
    }

    override fun onLoginSuccess(auth: Auth) {
        binding.signinLoadingPb.visibility= View.GONE
        saveJwt(this,auth.jwt)
        saveUserIdx(this,auth.userIdx)
        startMainActivity()
    }

    override fun onLoginFailure(code:Int, message:String) {
        binding.signinLoadingPb.visibility= View.GONE

        when(code) {
            2015,2019,3014-> {
                binding.signinErrorTv.visibility=View.VISIBLE
                binding.signinErrorTv.text=message
            }
        }
    }
}