package com.mimi.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mimi.Dto.Party;
import com.mimi.Dto.Store;
import com.mimi.Dto.User;
import com.mimi.Service.PartyService;
import com.mimi.Service.UserService;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/party")
public class PartyController {

	@Autowired
	private PartyService partyService;
	
	@Autowired
	private UserService userService;


	@PostMapping("/create")
	@ApiOperation(value = "모임 생성")
	public ResponseEntity<HashMap<String, Object>> createParty(@RequestBody Party party) {
		System.out.println("create Controller");
		System.out.println(party.getPName());

		try {
			HashMap<String, Object> map = new HashMap<>();
			Party partyCreated = partyService.createParty(party);

			if (partyCreated == null) {
				map.put("party", "fail");
			} else {
				map.put("party", partyCreated);
			}

			return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/{id}")
	@ApiOperation(value = "id로 모임 가져오기")
	public ResponseEntity<HashMap<String, Object>> getParty(@PathVariable("id") String id) {
		System.out.println("getParty Controller");
		try {
			HashMap<String, Object> map = new HashMap<>();

			Party partyInfo = partyService.getParty(id);
			map.put("Party", partyInfo);

			return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping(value = "/join")
	@ApiOperation(value = "모임 참여하기")
	public ResponseEntity<?> joinParty(@RequestParam String uid, @RequestParam String pid) {
		System.out.println("joinParty Controller");
		try {
			Party party = partyService.getParty(pid);
			User user = userService.getUserinfo(uid).get();
			System.out.println(user+" "+party);
			
			List<Integer> updatePartyList = user.getPartyList();
			System.out.println(updatePartyList);
			updatePartyList.add(Integer.parseInt(pid));
			System.out.println("test");
			user.setPartyList(updatePartyList);
			System.out.println(user+" "+party);
			List<Integer> updateUserList = party.getUserList();
			updateUserList.add(Integer.parseInt(uid));
			user.setPartyList(updateUserList);
			
			System.out.println(user+" "+party);
			partyService.save(party);
			userService.update(user);
			System.out.println("모임 참여 완료");
			return new ResponseEntity<>("모임 참여 완료", HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("error");
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	@PostMapping(value = "/out")
	@ApiOperation(value = "모임 탈퇴하기")
	public ResponseEntity<?> outParty(@RequestParam String uid, @RequestParam String pid) {
		System.out.println("outParty Controller");
		try {
			Party party = partyService.getParty(pid);
			User user = userService.getUserinfo(uid).get();
			
			List<Integer> updatePartyList = user.getPartyList();
			for(int i = 0;i<updatePartyList.size();i++) {
				if(updatePartyList.get(i)==Integer.parseInt(pid)) {
					updatePartyList.remove(i);
					break;
				}
			}
			user.setPartyList(updatePartyList);
			
			List<Integer> updateUserList = party.getUserList();
			for(int i = 0;i<updateUserList.size();i++) {
				if(updateUserList.get(i)==Integer.parseInt(uid)) {
					updateUserList.remove(i);
					break;
				}
			}
			party.setUserList(updateUserList);
			partyService.save(party);
			userService.update(user);

			return new ResponseEntity<>("모임 탈퇴 완료", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping(value = "/list/{id}")
	@ApiOperation(value = "유저 id로 모임 목록 가져오기")
	public ResponseEntity<?> getPartylist(@PathVariable("id") String id) {
		System.out.println("getPartylist Controller");
		try {
			Optional<User> user = userService.getUserinfo(id);
			List<Integer> partyid = user.get().getPartyList();
			List<Party> partylist = new LinkedList<Party>();
			for(int i = 0;i<partyid.size();i++) {
				partylist.add(partyService.getParty(String.valueOf(partyid.get(i))));
			}
			return new ResponseEntity<>(partylist, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/delete")
	@ApiOperation(value = "모임 삭제")
	public ResponseEntity<HashMap<String, Object>> deleteParty(@RequestBody Party party) {
		System.out.println("deleteParty Controller");
		try {
			HashMap<String, Object> map = new HashMap<>();
			partyService.deleteParty(party);
			map.put("Party", "deleted");

			return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	
	//약속
	
	@PostMapping(value = "/promiseUpdate")
	@ApiOperation(value = "약속 생성 및 수정")
	public ResponseEntity<?> promiseUpdate(@RequestParam Store store, @RequestParam String pid, @RequestParam Date date) {
		System.out.println("약속 생성 및 수정");
		try {
			Party party = partyService.getParty(pid);
			party.setPromiseStore(store);
			party.setPromiseTime(date);
			partyService.save(party);
			return new ResponseEntity<>(party, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping(value = "/promiseDelete")
	@ApiOperation(value = "약속 삭제")
	public ResponseEntity<?> promiseDelete(@RequestParam String pid) {
		System.out.println("약속 삭제");
		try {
			Party party = partyService.getParty(pid);
			party.setPromiseStore(null);
			partyService.save(party);
			return new ResponseEntity<>(party, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

}
