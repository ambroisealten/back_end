/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.controller.business.SkillBusinessController;
import fr.alten.ambroiseJEE.model.beans.ApplicantForum;
import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Mobility;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.ApplicantForumRepository;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.Nationality;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

/**
 *
 * @author Andy Chabalier
 *
 */
@Service
public class ApplicantForumEntityController {

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Method to validate if the mail math with the mail pattern
	 *
	 * @param emailStr the string to validate
	 * @return true if the string match with the mail pattern
	 * @author Andy Chabalier
	 */
	private static boolean validateMail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	@Autowired
	private ApplicantForumRepository applicantForumRepository;

	@Autowired
	private UserEntityController userEntityController;

	@Autowired
	private DiplomaEntityController diplomaEntityController;

	@Autowired
	private EmployerEntityController employerEntityController;

	@Autowired
	private JobEntityController jobEntityController;

	@Autowired
	private MobilityEntityController mobilityEntityController;

	@Autowired
	private SkillBusinessController skillBusinessController;

	/**
	 * Method to create a Applicant. Applicant type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jApplicant JsonNode with all Applicant parameters, except its type
	 *                   (name, mail, job, monthlyWage, startDate)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the Applicant is created
	 * @throws Exception @see ParseException if the date submitted hasn't a good
	 *                   format
	 * @author Andy Chabalier
	 */
	public HttpException createApplicant(JsonNode jApplicant) throws ParseException {
		// if the mail don't match with the mail pattern
		String applicantMail = jApplicant.get("mail").textValue();
		if (!validateMail(applicantMail)) {
			return new UnprocessableEntityException();
		}

		ApplicantForum newApplicant = new ApplicantForum();
		newApplicant.setName(jApplicant.get("name").textValue());
		newApplicant.setSurname(jApplicant.get("surname").textValue());
		newApplicant.setMonthlyWage(Integer.parseInt(jApplicant.get("monthlyWage").textValue()));
		newApplicant.setMail(applicantMail);
		List<String> docList = new ArrayList<String>();
		JsonNode docNode = jApplicant.get("docs");
		for (JsonNode doc : docNode) {
			docList.add(doc.get("url").textValue());
		}
		newApplicant.setUrlDocs(docList);

		Optional<User> personInCharge = userEntityController.getUserByMail(jApplicant.get("managerMail").textValue());
		if (personInCharge.isPresent()) {
			newApplicant.setPersonInChargeMail(personInCharge.get().getMail());
		}

		Optional<Diploma> diploma = diplomaEntityController.getDiplomaByNameAndYearOfResult(
				jApplicant.get("diplomaName").textValue(), jApplicant.get("diplomaYear").textValue());
		if (diploma.isPresent()) {
			newApplicant.setHighestDiploma(diploma.get().get_id().toString());
		}

		Optional<Job> job = jobEntityController.getJob(jApplicant.get("job").textValue());
		if (job.isPresent()) {
			newApplicant.setJob(job.get().getTitle());
		}

		Optional<Employer> employer = employerEntityController.getEmployer(jApplicant.get("employer").textValue());
		if (employer.isPresent()) {
			newApplicant.setEmployer(employer.get().getName());
		}

		newApplicant.setMobilities(getAllMobilities(jApplicant.get("mobilities")));
		newApplicant.setPhoneNumber(jApplicant.get("phoneNumber").textValue());

		newApplicant.setStartAt(jApplicant.get("canStartAt").textValue());
		newApplicant.setGrade(jApplicant.get("grade").textValue());
		newApplicant.setCommentary(jApplicant.get("commentary").textValue());
		newApplicant.setContractType(jApplicant.get("contractType").textValue());
		newApplicant.setContractDuration(jApplicant.get("contractDuration").textValue());

		List<String> skills = new ArrayList<String>();
		JsonNode skillNode = jApplicant.get("skills");
		for (JsonNode skill : skillNode) {
			Optional<Skill> SkillOptional = skillBusinessController.getSkill(skill,UserRole.MANAGER_ADMIN);
			if (SkillOptional.isPresent()) {
				skills.add(SkillOptional.get().getName());
			}
		}
		newApplicant.setSkills(skills);

		newApplicant.setVehicule(Boolean.getBoolean(jApplicant.get("hasVehicule").textValue()));
		newApplicant.setDriverLicense(Boolean.getBoolean(jApplicant.get("hasPermis").textValue()));
		JsonNode nationality = jApplicant.get("nationality");
		newApplicant.setNationality(Nationality.valueOf(nationality.isNull() ? "NONE" : nationality.textValue()));

		try {
			applicantForumRepository.save(newApplicant);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();

	}

	/**
	 * Method to delete a Applicant. Applicant type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jApplicant contains at least the Applicant's name
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database and {@link OkException} if the applicant is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteApplicant(JsonNode jApplicant) {
		Optional<ApplicantForum> optionalApplicant = applicantForumRepository
				.findByMail(jApplicant.get("mail").textValue());
		if (optionalApplicant.isPresent()) {
			ApplicantForum applicant = optionalApplicant.get();
			applicant.setName("Demissionaire");
			applicant.setSurname(null);
			applicant.setMail("deactivated" + System.currentTimeMillis() + "@deactivated.com");
			applicant.setEmployer(null);
			applicant.setUrlDocs(null);
			applicant.setMonthlyWage(Float.NaN);
			applicant.setHighestDiploma(null);
			applicant.setPersonInChargeMail(null);
			applicant.setJob(null);
			applicant.setMobilities(null);
			applicant.setPhoneNumber(null);
			applicant.setStartAt(null);
			applicant.setGrade(null);
			applicant.setCommentary(null);
			applicant.setContractType(null);
			applicant.setContractDuration(null);
			applicant.setSkills(null);
			applicant.setVehicule(false);
			applicant.setDriverLicense(false);
			applicant.setNationality(Nationality.NONE);

			applicantForumRepository.save(applicant);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * Get a List of Mobility object given a list of JsonNode(represented as a
	 * String)
	 *
	 * @param jMobility the String corresponding to a JsonNode corresponding to the
	 *                  mobility part of a applicant
	 * @return A List of Mobility
	 * @author Lucas Royackkers
	 */
	private List<String> getAllMobilities(JsonNode jMobilities) {
		List<String> allMobilities = new ArrayList<String>();
		for (JsonNode mobility : jMobilities) {
			Mobility mobilityToFind = new Mobility();
			mobilityToFind.setPlaceName(mobility.get("placeName").textValue());
			mobilityToFind.setPlaceType(mobility.get("placeType").textValue());
			mobilityToFind.setRadius(Integer.parseInt(mobility.get("radius").textValue()));
			mobilityToFind.setUnit(mobility.get("unit").textValue());

			Optional<Mobility> optionalMobility = mobilityEntityController.getMobility(mobilityToFind);
			if (optionalMobility.isPresent()) {
				allMobilities.add(optionalMobility.get().get_id().toString());
			}
		}
		return allMobilities;
	}

	/**
	 * @param mail the applicant's mail to fetch
	 * @return the optional with the applicant fetched
	 * @author Andy Chabalier
	 */
	public Optional<ApplicantForum> getApplicantByMail(String mail) {
		return applicantForumRepository.findByMail(mail);
	}

	/**
	 * @return the list of all applicants
	 * @author Andy Chabalier
	 */
	public List<ApplicantForum> getApplicants() {
		return applicantForumRepository.findAll();
	}

	/**
	 * Method to update a Applicant. Applicant type will be defined by business
	 * controllers ahead of this object.
	 *
	 * @param jApplicant JsonNode containing all parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database and {@link OkException} if the applicant is updated
	 * @throws ParseException
	 * @author Andy Chabalier
	 */
	public HttpException updateApplicant(JsonNode jApplicant) throws ParseException {
		Optional<ApplicantForum> optionalApplicant = applicantForumRepository
				.findByMail(jApplicant.get("oldMail").textValue());
		if (optionalApplicant.isPresent()) {
			ApplicantForum applicant = optionalApplicant.get();
			applicant.setName(jApplicant.get("name").textValue());
			applicant.setSurname(jApplicant.get("surname").textValue());
			applicant.setMonthlyWage(Integer.parseInt(jApplicant.get("wage").textValue()));

			applicant.setMail(jApplicant.get("mail").textValue());

			Optional<User> personInCharge = userEntityController
					.getUserByMail(jApplicant.get("managerMail").textValue());
			if (personInCharge.isPresent()) {
				applicant.setPersonInChargeMail(personInCharge.get().getMail());
			}

			Optional<Diploma> diploma = diplomaEntityController.getDiplomaByNameAndYearOfResult(
					jApplicant.get("diplomaName").textValue(), jApplicant.get("diplomaYear").textValue());
			if (diploma.isPresent()) {
				applicant.setHighestDiploma(diploma.get().get_id().toString());
			}

			Optional<Job> job = jobEntityController.getJob(jApplicant.get("job").textValue());
			if (job.isPresent()) {
				applicant.setJob(job.get().getTitle());
			}

			Optional<Employer> employer = employerEntityController.getEmployer(jApplicant.get("employer").textValue());
			if (employer.isPresent()) {
				applicant.setEmployer(employer.get().getName());
			}

			List<String> docList = new ArrayList<String>();
			JsonNode docNode = jApplicant.get("docs");
			for (JsonNode doc : docNode) {
				docList.add(doc.get("url").textValue());
			}
			applicant.setUrlDocs(docList);

			applicant.setMobilities(getAllMobilities(jApplicant.get("mobilities")));
			applicant.setPhoneNumber(jApplicant.get("phoneNumber").textValue());

			applicant.setStartAt(jApplicant.get("canStartAt").textValue());
			applicant.setGrade(jApplicant.get("grade").textValue());
			applicant.setCommentary(jApplicant.get("commentary").textValue());
			applicant.setContractType(jApplicant.get("contractType").textValue());
			applicant.setContractDuration(jApplicant.get("contractDuration").textValue());

			List<String> skills = new ArrayList<String>();
			JsonNode skillNode = jApplicant.get("skills");
			for (JsonNode skill : skillNode) {
				Optional<Skill> SkillOptional = skillBusinessController.getSkill(skill,UserRole.MANAGER_ADMIN);
				if (SkillOptional.isPresent()) {
					skills.add(SkillOptional.get().getName());
				}
			}
			applicant.setSkills(skills);

			applicant.setVehicule(Boolean.getBoolean(jApplicant.get("hasVehicule").textValue()));
			applicant.setDriverLicense(Boolean.getBoolean(jApplicant.get("hasPermis").textValue()));
			applicant.setNationality(Nationality.valueOf(jApplicant.get("nationality").textValue()));

			applicantForumRepository.save(applicant);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}
}
