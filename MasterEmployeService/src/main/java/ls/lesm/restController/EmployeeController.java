package ls.lesm.restController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ls.lesm.exception.DuplicateEntryException;
import ls.lesm.model.Address;
import ls.lesm.model.Designations;
import ls.lesm.model.EmployeePhoto;
import ls.lesm.model.MasterEmployeeDetails;
import ls.lesm.model.Salary;
import ls.lesm.model.SecondaryManager;
import ls.lesm.payload.request.EmployeeDetailsRequest;
import ls.lesm.payload.request.EmployeeDetailsUpdateRequest;
import ls.lesm.payload.response.EmpCorrespondingDetailsResponse;
import ls.lesm.payload.response.EmployeeDetailsResponse;
import ls.lesm.payload.response.PageResponse;
import ls.lesm.payload.response.SupervisorDropDown;
import ls.lesm.repository.AddressRepositoy;
import ls.lesm.repository.AddressTypeRepository;
import ls.lesm.repository.DepartmentsRepository;
import ls.lesm.repository.DesignationsRepository;
import ls.lesm.repository.EmployeePhotoRepo;
import ls.lesm.repository.EmployeeTypeRepository;
import ls.lesm.repository.MasterEmployeeDetailsRepository;
import ls.lesm.repository.SalaryRepository;
import ls.lesm.repository.SecondaryManagerRepository;
import ls.lesm.repository.SubDepartmentsRepository;
import ls.lesm.repository.TechnologyRepository;
import ls.lesm.service.IImageService;
import ls.lesm.service.impl.EmployeeDetailsServiceImpl;

@RestController
@RequestMapping("/api/v1/emp")
@CrossOrigin("*")
public class EmployeeController {

	@Autowired
	private EmployeeDetailsServiceImpl employeeDetailsService;

	@Autowired
	private MasterEmployeeDetailsRepository masterEmployeeDetailsRepository;
	@Autowired
	private DepartmentsRepository departmentsRepository;
	@Autowired
	private SubDepartmentsRepository subDepartmentsRepositorye;
	@Autowired
	private DesignationsRepository designationsRepository;
	@Autowired
	private EmployeeTypeRepository employeeTypeRepository;

	@Autowired
	private AddressRepositoy addressRepositoy;

	@Autowired
	private AddressTypeRepository addressTypeRepository;

	@Autowired
	private IImageService imageService;

	@Autowired
	private EmployeePhotoRepo employeePhotoRepo;

	@Autowired
	private SalaryRepository salaryRepo;

	@Autowired
	private SecondaryManagerRepository secondaryManagerRepository;

	@Autowired
	private TechnologyRepository technologyRepository;

	// UMER

	@PostMapping("/insert-emp-details")
	public ResponseEntity<?> empDetailsInsertion(@RequestParam(required = false, defaultValue = "0") Integer subVId,
			@RequestParam(required = false) Integer departId, @RequestParam(required = false) Integer subDepartId,
			@RequestParam(required = false) Integer desgId, @RequestParam Integer addressTypeId,
			@RequestParam(required = false) Integer technology1Id, @RequestParam(required = false) Integer typeId,
			@RequestBody @Valid EmployeeDetailsRequest empReq) {

		MasterEmployeeDetails emp = this.masterEmployeeDetailsRepository
				.findByLancesoft(empReq.getMasterEmployeeDetails().getLancesoft());
		if (emp != null) {
			return new ResponseEntity<>(
					new DuplicateEntryException("Employee with this employee Id alreday exist in database"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		MasterEmployeeDetails employee = this.masterEmployeeDetailsRepository.save(empReq.getMasterEmployeeDetails());

		this.masterEmployeeDetailsRepository.findById(employee.getEmpId()).map(id -> {
			empReq.getSalary().setMasterEmployeeDetails(id);
			return id;
		});

		this.masterEmployeeDetailsRepository.findById(employee.getEmpId()).map(id -> {
			empReq.getAddress().setMasterEmployeeDetails(id);
			return id;
		});

		this.masterEmployeeDetailsRepository.findById(subVId).map(id -> {
			empReq.getMasterEmployeeDetails().setSupervisor(id);
			return id;
		});

		this.departmentsRepository.findById(departId).map(id -> {
			empReq.getMasterEmployeeDetails().setDepartments(id);
			return id;
		});

		this.subDepartmentsRepositorye.findById(subDepartId).map(id -> {
			empReq.getMasterEmployeeDetails().setSubDepartments(id);
			return id;
		});
		this.designationsRepository.findById(desgId).map(id -> {
			empReq.getMasterEmployeeDetails().setDesignations(id);
			return id;
		});

		this.technologyRepository.findById(technology1Id).map(id -> {
			empReq.getMasterEmployeeDetails().setTechnology1(id);
			return id;
		});
		this.employeeTypeRepository.findById(typeId).map(id -> {
			empReq.getMasterEmployeeDetails().setEmployeeType(id);
			return id;
		});

		this.addressTypeRepository.findById(addressTypeId).map(id -> {
			empReq.getAddress().setAdressType(id);
			return id;
		});

		EmployeeDetailsRequest request = this.employeeDetailsService.insetEmpDetails(empReq);
		String lancesoft = request.getMasterEmployeeDetails().getLancesoft();
		return new ResponseEntity<>(lancesoft, HttpStatus.CREATED);

	}

//	// UMER
//	@GetMapping("/get-details-byId/{id}")
//	public ResponseEntity<EmployeesAtClientsDetails> getDetailsOfEmpAtClientById(@RequestParam int id) {
//
//		EmployeesAtClientsDetails clientDetails = employeesAtClientsDetailsRepository.findById(id).orElseThrow(
//				() -> new RecordNotFoundException("Client Details with this id '" + id + "' not exist in database"));
//
//		Optional<MasterEmployeeDetails> employee = this.masterEmployeeDetailsRepository
//				.findById(clientDetails.getMasterEmployeeDetails().getEmpId());
//		if (clientDetails.getPOEdate() == null) {
//			clientDetails.setTenure(ChronoUnit.MONTHS.between(clientDetails.getPOSdate(), LocalDate.now()));
//
//			employee.get().setStatus(EmployeeStatus.CLIENT);
//			masterEmployeeDetailsRepository.save(employee.get());
//		} else
//			employee.get().setStatus(EmployeeStatus.BENCH);
//
//		clientDetails.setTenure(ChronoUnit.MONTHS.between(clientDetails.getPOSdate(), clientDetails.getPOEdate()));
//
//		clientDetails.setTotalEarningAtclient(clientDetails.getClientSalary() * clientDetails.getTenure());
//		this.employeesAtClientsDetailsRepository.save(clientDetails);
//		return new ResponseEntity<EmployeesAtClientsDetails>(clientDetails, HttpStatus.ACCEPTED);
//	}

	// UMER
	@GetMapping("/repotees")
	public ResponseEntity<Map<String, Object>> getRepotees(Principal principal,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {

		MasterEmployeeDetails loggedInEmployee = this.masterEmployeeDetailsRepository
				.findByLancesoft(principal.getName());

		Page<MasterEmployeeDetails> allRepotees = this.masterEmployeeDetailsRepository
				.findBySupervisor(loggedInEmployee, PageRequest.of(pageNumber, pageSize));
		List<SecondaryManager> subordManager = this.secondaryManagerRepository.findBySecondaryManager(loggedInEmployee);

		List<PageResponse> repoteesList = new ArrayList<PageResponse>();
		Map<String, Object> response = new HashMap<>();

		for (MasterEmployeeDetails m : allRepotees) {
			PageResponse card = new PageResponse();

			card.setEmpId(m.getEmpId());
			card.setEmployeeName(StringUtils.capitalize(m.getFirstName().toLowerCase()) + " "
					+ StringUtils.capitalize(m.getLastName().toLowerCase()));
			card.setLancesoftId(m.getLancesoft());
			try {
				card.setStatus(m.getStatus());
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			try {
				// card.setManagerName(StringUtils.capitalize(m.getSupervisor().getFirstName().toLowerCase())
				// + " " +
				// StringUtils.capitalize(m.getSupervisor().getLastName().toLowerCase()));
				card.setManagerName("You");
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			try {
				Optional<SecondaryManager> subManager = this.secondaryManagerRepository.findByEmployee(m);
				if (subManager.isPresent()) {
					card.setSubordinateManagerName(
							StringUtils.capitalize(subManager.get().getSecondaryManager().getFirstName().toLowerCase())
									+ " " + StringUtils.capitalize(
											subManager.get().getSecondaryManager().getLastName().toLowerCase()));
				}
				if (subManager.isEmpty()) {
					card.setSubordinateManagerName("Not Assigned");
				}
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			try {
				card.setDesignation(StringUtils.capitalize(m.getDesignations().getDesgNames()));
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			try {
				card.setPhoto(m.getEmployeePhoto().getProfilePic());
			} catch (NullPointerException npe) {

			}
			repoteesList.add(card);
		}
		for (SecondaryManager s : subordManager) {
			PageResponse card = new PageResponse();
			card.setEmpId(s.getEmployee().getEmpId());
			card.setEmployeeName(StringUtils.capitalize(s.getEmployee().getFirstName().toLowerCase()) + " "
					+ StringUtils.capitalize(s.getEmployee().getLastName().toLowerCase()));
			card.setLancesoftId(s.getEmployee().getLancesoft());
			card.setSubordinateManagerName("You");
			try {
				card.setStatus(s.getEmployee().getStatus());
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			try {
				if (s.getEmployee().getSupervisor() != null) {
					card.setManagerName(StringUtils
							.capitalize(s.getEmployee().getSupervisor().getFirstName().toLowerCase()) + " "
							+ StringUtils.capitalize(s.getEmployee().getSupervisor().getLastName().toLowerCase()));
				} else if (s.getEmployee().getSupervisor() == null) {
					card.setManagerName("Not Assigned");
				}
			} catch (NullPointerException npe) {
			}
			try {
				card.setDesignation(
						StringUtils.capitalize(s.getEmployee().getDesignations().getDesgNames().toLowerCase()));
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			try {
				card.setPhoto(s.getEmployee().getEmployeePhoto().getProfilePic());
			} catch (NullPointerException npe) {
				// TODO: handle exception
			}
			repoteesList.add(card);
		}
		Page<PageResponse> pagEmps = new PageImpl<>(repoteesList);
		response.put("Employees", pagEmps.getContent());
		response.put("currentPage", allRepotees.getNumber());
		response.put("totalItems", allRepotees.getTotalElements());
		response.put("totalPage", allRepotees.getTotalPages() - 1);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// UMER
	@GetMapping("/get-under-emps")
	public ResponseEntity<List<EmployeeDetailsResponse>> getUnderEmps(@RequestParam(value = "id") int id) {

		List<EmployeeDetailsResponse> emps = this.masterEmployeeDetailsRepository.getEmpDetails(id);
		List<EmployeeDetailsResponse> filtered = emps.stream().filter(p -> p.getStatus().equals("BENCH")
				|| p.getStatus().equals("MANAGMENT") || p.getStatus().equals("CLIENT")).collect(Collectors.toList());

		return new ResponseEntity<List<EmployeeDetailsResponse>>(filtered, HttpStatus.OK);
	}

	// UMER
	@GetMapping("/get-emp-crosspnd-details")
	// need to be replace with new api (/details)
	public ResponseEntity<EmpCorrespondingDetailsResponse> empCorresDetails(@RequestParam int id) {
		EmpCorrespondingDetailsResponse e = new EmpCorrespondingDetailsResponse();

		EmpCorrespondingDetailsResponse details = this.employeeDetailsService.getEmpCorresDetails(e, id);

		return new ResponseEntity<EmpCorrespondingDetailsResponse>(details, HttpStatus.OK);
	}

	// UMER
	@GetMapping("/get-supvisor-dropdown")
	public ResponseEntity<List<SupervisorDropDown>> getSupDropDown(@RequestParam int id) {
		Optional<Designations> desg = this.designationsRepository.findById(id);
		int desgPkId = desg.get().getDesignations().getDesgId();
		System.out.println("---------------" + desgPkId);
		List<SupervisorDropDown> ab = this.masterEmployeeDetailsRepository
				.supDropDown(desg.get().getDesignations().getDesgId());
		return new ResponseEntity<List<SupervisorDropDown>>(ab, HttpStatus.OK);
	}

	// UMER

	// UMER
	@PutMapping("/update-emp")
	public ResponseEntity<EmployeeDetailsUpdateRequest> updateEmployeeDetails(@RequestParam int id,
			@RequestBody EmployeeDetailsUpdateRequest empReq) {

		this.employeeDetailsService.updateEmployee(empReq, id);

		return new ResponseEntity<EmployeeDetailsUpdateRequest>(HttpStatus.ACCEPTED);
	}
//UMER

	@GetMapping("/download-photo")
	public Object downloadImage(@RequestParam int empId) throws FileNotFoundException, IOException {
		return this.imageService.download(empId);
	}

	// firebase bucket api
	// UMER
	@PostMapping("/photo-upload")
	public ResponseEntity create(@RequestParam(name = "file") MultipartFile files, @RequestParam String lancesoftId) {

		try {
			String fileName = imageService.save(files);

			String imageUrl = imageService.getImageUrl(fileName);
			System.out.println("------------------" + imageUrl);
			EmployeePhoto photo = new EmployeePhoto();
			MasterEmployeeDetails employee = this.masterEmployeeDetailsRepository.findByLancesoft(lancesoftId);

			this.masterEmployeeDetailsRepository.findById(employee.getEmpId()).map(id -> {
				photo.setMasterEmployeeDetails(id);
				return id;
			});

			photo.setProfilePic(imageUrl);
			EmployeePhoto saved = this.employeePhotoRepo.save(photo);
			this.employeePhotoRepo.findById(saved.getDocId()).map(docId -> {
				employee.setEmployeePhoto(docId);
				return this.masterEmployeeDetailsRepository.save(employee);
			});
		} catch (Exception e) {
			e.printStackTrace();

		}

		return ResponseEntity.ok().build();
	}

	@PutMapping("/update-photo")
	public ResponseEntity updatePhoto(@RequestParam(name = "file") MultipartFile files,
			@RequestParam String lancesoftId) throws IOException {
		MasterEmployeeDetails employee = this.masterEmployeeDetailsRepository.findByLancesoft(lancesoftId);

		try {

			String fileName = imageService.save(files);

			String imageUrl = imageService.getImageUrl(fileName);
			System.out.println("------------------" + imageUrl);

			Optional<EmployeePhoto> saved = this.employeePhotoRepo.findByMasterEmployeeDetails(employee);
			if (saved.isPresent()) {
				this.imageService.delete(employee.getEmpId());
				saved.get().setProfilePic(imageUrl);
				this.employeePhotoRepo.save(saved.get());
			} else {
				EmployeePhoto photo = new EmployeePhoto();
				photo.setProfilePic(imageUrl);
				photo.setMasterEmployeeDetails(employee);
				employee.setEmployeePhoto(photo);
				this.masterEmployeeDetailsRepository.save(employee);
				this.employeePhotoRepo.save(photo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok().build();
	}

	// firebase bucket api
	// UMER
	@DeleteMapping("/photo-delete")
	public ResponseEntity delete(@RequestParam int empId) throws IOException {

		// for (MultipartFile file : files) {
		this.imageService.delete(empId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/update-emp-req")
	public ResponseEntity<EmployeeDetailsUpdateRequest> updateEmployeeReq(@RequestParam int id) {
		EmployeeDetailsUpdateRequest req = new EmployeeDetailsUpdateRequest();
		Optional<MasterEmployeeDetails> emp = this.masterEmployeeDetailsRepository.findById(id);
		req.setMasterEmployeeDetails(emp.get());
		Address add = this.addressRepositoy.findByEmpId(id);
		try {
			req.setDepartments(emp.get().getDepartments().getDepartId());
		} catch (NullPointerException npe) {

		}
		try {
			req.setTechnology1(emp.get().getTechnology1().getId());
		} catch (NullPointerException npe) {

		}
		try {
			req.setDesignations(emp.get().getDesignations().getDesgId());
		} catch (NullPointerException npe) {

		}
		try {
			req.setEmployeeType(emp.get().getEmployeeType().getEmpTypeId());
		} catch (NullPointerException npe) {

		}
		try {
			req.setSubDepartments(emp.get().getSubDepartments().getSubDepartId());
		} catch (NullPointerException npe) {

		}
		try {
			req.setSupervisor(emp.get().getSupervisor().getEmpId());
		} catch (NullPointerException npe) {

		}
		try {
			req.setSupervisorDesig(emp.get().getSupervisor().getDesignations().getDesgId());
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		try {
//		List<Salary> salaries = this.salaryRepo.findsBymasterEmployeeDetails_Id(emp.get().getEmpId());
//		List<Integer> salIds = salaries.stream().map(Salary::getSalId).collect(Collectors.toList());
//		int latestId = Collections.max(salIds);

			List<Salary> salaries = this.salaryRepo.findsBymasterEmployeeDetails_Id(emp.get().getEmpId());

			if (salaries.size() == 1) {// if there is only one directly setting up salary
				req.setSalary(salaries.get(0).getSalary());

			} else {

				HashMap<LocalDate, String> salaryDates = new HashMap<LocalDate, String>();// taken key,val bcz want to
																							// know from which col we're
																							// getting sals
				for (Salary s : salaries) {
					if (s.getUpdatedAt() == null) {
						salaryDates.put(s.getCreatedAt(), "Created");
					} else {
						salaryDates.put(s.getUpdatedAt(), "Updated");
					}
				}
				LocalDate maxDate = salaryDates.keySet().stream().filter(e -> e.isBefore(LocalDate.now()))
						.max(LocalDate::compareTo).orElse(null);// found latest sal date which is before current date
				String val = salaryDates.get(maxDate);// got val of latest sal key

				if (val.equals("Created")) {// using val of latest sal key,finding record with that col query mehtod
					List<Salary> currentSals = this.salaryRepo.findByCreatedAtAndMasterEmployeeDetails(maxDate,
							emp.get());// return list bcz ther might be multiple dame date
					req.setSalary(currentSals.stream().findFirst().get().getSalary());//
				} else {
					List<Salary> currentSals = this.salaryRepo.findByUpdatedAtAndMasterEmployeeDetails(maxDate,
							emp.get());
					req.setSalary(currentSals.stream().findFirst().get().getSalary());
				}
			}

			// req.setSalary(this.salaryRepo.findById(latestId).get().getSalary());
		} catch (NoSuchElementException e) {
			// TODO: handle exception
		}
		req.setAddress(add);

		return new ResponseEntity<EmployeeDetailsUpdateRequest>(req, HttpStatus.OK);

	}

	@PostMapping("/subordinate-manager")
	public ResponseEntity<SecondaryManager> setSecondaryManager(@RequestParam int empId, @RequestParam int supervisorId,
			@RequestParam(required = false, defaultValue = "false") boolean flag) {

		this.employeeDetailsService.assignSencondSupervisor(empId, supervisorId, flag);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}