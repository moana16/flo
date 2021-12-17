package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator


class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    val info = arrayListOf("저장한 곡","음악파일","저장앨범")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter=LockerViewpagerAdapter(this)
        binding.lockerContentVp.adapter=lockerAdapter
        TabLayoutMediator(binding.lockerContentTl,binding.lockerContentVp) {
            tab,position->
            tab.text=info[position]
        }.attach()

        binding.lockerSigninBtn.setOnClickListener{
            startActivity(Intent(activity,SignInActivity::class.java))
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initView() {
        val jwt = getUserIdx(requireContext())
        val userDB = SongDB.getInstance(requireContext())!!
        val name : String ?=userDB.UserDao().getUsername(getUserIdx(requireContext()))

        if(jwt==0) {
            binding.lockerSigninBtn.text="로그인"
            binding.lockerSigninBtn.setOnClickListener {
                startActivity(Intent(activity,SignInActivity::class.java))
            }
            binding.lockerUserNameTv.visibility=View.GONE
            binding.lockerNimTv.visibility=View.GONE
        }
        else {
            binding.lockerSigninBtn.text="로그아웃"
            binding.lockerSigninBtn.setOnClickListener {
                logout()
                startActivity(Intent(activity,MainActivity::class.java))
            }
            binding.lockerUserNameTv.visibility=View.VISIBLE
            binding.lockerNimTv.visibility=View.VISIBLE
            binding.lockerUserNameTv.text=name.toString()
        }
    }




    private fun logout() {
        val spf=activity?.getSharedPreferences("auth",AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("userIdx")
        editor.apply()
    }
}