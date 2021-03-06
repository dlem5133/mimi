package com.mimi.Controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mimi.Dto.Boss;
import com.mimi.Service.BossService;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/boss")
public class BossController {

	@Autowired
	private BossService bossService;

	@PostMapping("/create")
	@ApiOperation(value = "사장님 등록")
	public ResponseEntity<HashMap<String, Object>> createBoss(@RequestBody Boss boss) {
		System.out.println("upload Controller");
		System.out.println(boss.getId());

		try {
			HashMap<String, Object> map = new HashMap<>();
			Boss bossCreated = bossService.createBoss(boss);

			if (boss == null) {
				map.put("boss", "fail");
			} else {
				map.put("boss", bossCreated);
			}

			return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/{id}")
	@ApiOperation(value = "사장님 id 로 확인")
	public ResponseEntity<HashMap<String, Object>> getBoss(@PathVariable("id") String id) {
		System.out.println("getBoss Controller");
		try {
			HashMap<String, Object> map = new HashMap<>();

//			Optional<Dining> diningInfo = diningService.getDining(id);
			Optional<Boss> bossInfo = bossService.getBoss(id);
			map.put("boss", bossInfo.get());

			return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

	}

	@PostMapping("/delete")
	@ApiOperation(value = "사장님 탈퇴")
	public ResponseEntity<HashMap<String, Object>> deleteBoss(@RequestBody Boss boss) {
		System.out.println("delete Controller");
		try {
			HashMap<String, Object> map = new HashMap<>();
			bossService.deleteBoss(boss);
			map.put("boss", "removed");

			return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

}
