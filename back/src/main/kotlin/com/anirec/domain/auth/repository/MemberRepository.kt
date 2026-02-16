package com.anirec.domain.auth.repository

import com.anirec.domain.auth.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, String>
