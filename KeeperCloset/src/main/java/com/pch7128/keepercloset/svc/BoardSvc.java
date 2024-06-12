package com.pch7128.keepercloset.svc;


import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pch7128.keepercloset.dto.Battach;
import com.pch7128.keepercloset.dto.BattachDTO;
import com.pch7128.keepercloset.dto.Member;
import com.pch7128.keepercloset.dto.Review;
import com.pch7128.keepercloset.repository.BattachRepository;
import com.pch7128.keepercloset.repository.ReviewRepository;
import com.pch7128.keepercloset.repository.RvRepository;
import com.pch7128.keepercloset.dto.ReviewResponseDTO;

import jakarta.transaction.Transactional;

@Service
public class BoardSvc {
	
	@Autowired
	private ReviewRepository reviewre;
	@Autowired
	private BattachRepository bAttre;
	@Autowired
	private static final String upPath="/Users/eeeun/Desktop/git/KeeperCloset/KeeperCloset/src/main/resources/static/images";
	
	
	@Transactional
	public void saveReview(MultipartFile[] files,Review review, Member m) throws Exception{
		review.setMember(m);
		List<Battach> flist=battachList(files,review);
		review.setBattach(flist);
		LocalDate currentDate = LocalDate.now();
		review.setBoard_posted(Date.valueOf(currentDate));
		Review r=reviewre.save(review);
		
	}

	public List<Battach> battachList(MultipartFile[] files,Review review) throws IllegalStateException,IOException{
		
		List<Battach> flist = new ArrayList<Battach>();
		
		for(MultipartFile mf : files) {
			if(mf.isEmpty()) break;
			
			Battach bAtt = new Battach();
			UUID u=UUID.randomUUID();
			String fname=mf.getOriginalFilename();
			String savedfname=u.toString()+"_"+fname;
			bAtt = bAtt.builder()
					.fname(fname)
					.savedfname(savedfname)
					.review(review)
					.build();
			flist.add(bAtt);
			mf.transferTo(new File(upPath,bAtt.getSavedfname()));
			
		}
		bAttre.saveAll(flist);
		return flist;
	}
	
	public Page<ReviewResponseDTO> reviewPaging(Pageable pageable){
		
		Page<Review> reviews=reviewre.findWithBattach(pageable);
		
		List<ReviewResponseDTO> reviewDTO = reviews.stream()
				.map(review -> {
					ReviewResponseDTO dto = new ReviewResponseDTO(review);
					List<BattachDTO> bttDTO = review.getBattach().stream()
							.map(BattachDTO::new)
							.collect(Collectors.toList());
					dto.setBattach(bttDTO);
					return dto;
				}).collect(Collectors.toList());
		
		return new PageImpl<>(reviewDTO,pageable,reviews.getTotalElements());
	}
}
