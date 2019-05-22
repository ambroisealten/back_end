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

import fr.alten.ambroiseJEE.model.beans.ApplicantForum;
import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Mobility;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.model.dao.ApplicantForumRepository;
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
	private static boolean validateMail(final String emailStr) {
		final Matcher matcher = ApplicantForumEntityController.VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
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
	private SkillEntityController skillEntityController;

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
	public HttpException createApplicant(final JsonNode jApplicant) throws ParseException {
		// if the mail don't match with the mail pattern
		try {
			final String applicantMail = jApplicant.get("mail").textValue();

			if (!ApplicantForumEntityController.validateMail(applicantMail)) {
				return new UnprocessableEntityException();
			}

			final ApplicantForum newApplicant = new ApplicantForum();
			newApplicant.setName(jApplicant.get("name").textValue());
			newApplicant.setSurname(jApplicant.get("surname").textValue());
			newApplicant.setMonthlyWage(Integer.parseInt(jApplicant.get("monthlyWage").textValue()));
			newApplicant.setMail(applicantMail);

			final User personInCharge = this.userEntityController
					.getUserByMail(jApplicant.get("managerMail").textValue());
			newApplicant.setPersonInChargeMail(personInCharge.getMail());

			final String highestDiploma = jApplicant.get("highestDiploma").textValue();
			final String highestDiplomaYear = jApplicant.get("highestDiplomaYear").textValue();
			Diploma diploma;
			try {
				diploma = this.diplomaEntityController.getDiplomaByNameAndYearOfResult(highestDiploma,
						highestDiplomaYear);
			} catch (final ResourceNotFoundException e) {
				diploma = this.diplomaEntityController.createDiploma(highestDiploma, highestDiplomaYear);
			}

			newApplicant.setHighestDiploma(diploma.getName());
			newApplicant.setHighestDiplomaYear(diploma.getYearOfResult());

			final String jobName = jApplicant.get("job").textValue();
			Job job;
			try {
				job = this.jobEntityController.getJob(jobName);
			} catch (final ResourceNotFoundException e) {
				job = this.jobEntityController.createJob(jobName);
			}
			newApplicant.setJob(job.getTitle());

			final String employerName = jApplicant.get("employer").textValue();
			Employer employer;
			try {
				employer = this.employerEntityController.getEmployer(employerName);
			} catch (final ResourceNotFoundException e) {
				employer = this.employerEntityController.createEmployer(employerName);
			}

			newApplicant.setEmployer(employer.getName());

			newApplicant.setMobilities(getAllMobilities(jApplicant.get("mobilities")));
			newApplicant.setPhoneNumber(jApplicant.get("phoneNumber").textValue());

			newApplicant.setStartAt(jApplicant.get("canStartAt").textValue());
			newApplicant.setGrade(jApplicant.get("grade").textValue());
			newApplicant.setCommentary(jApplicant.get("commentary").textValue());
			newApplicant.setContractType(jApplicant.get("contractType").textValue());
			newApplicant.setContractDuration(jApplicant.get("contractDuration").textValue());

			final List<String> skills = new ArrayList<String>();
			final JsonNode skillNode = jApplicant.get("skills");
			for (final JsonNode JSkill : skillNode) {
				final String skillName = JSkill.get("name").textValue();
				Skill skill;

				try {
					skill = this.skillEntityController.getSkill(skillName);
				} catch (final ResourceNotFoundException e) {
					skill = this.skillEntityController.createSkill(skillName, null).get();
				}

				skills.add(skill.getName());
			}
			newApplicant.setSkills(skills);

			newApplicant.setOpinion(jApplicant.get("opinion").textValue());

			newApplicant.setVehicule(Boolean.getBoolean(jApplicant.get("hasVehicule").textValue()));
			newApplicant.setDriverLicense(Boolean.getBoolean(jApplicant.get("hasPermis").textValue()));
			final JsonNode nationality = jApplicant.get("nationality");
			newApplicant.setNationality(Nationality.valueOf(nationality.isNull() ? "NONE" : nationality.textValue()));

			this.applicantForumRepository.save(newApplicant);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
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
	public HttpException deleteApplicant(final JsonNode jApplicant) {
		final Optional<ApplicantForum> optionalApplicant = this.applicantForumRepository
				.findByMail(jApplicant.get("mail").textValue());
		if (optionalApplicant.isPresent()) {
			final ApplicantForum applicant = optionalApplicant.get();
			applicant.setName("Demissionaire");
			applicant.setSurname(null);
			applicant.setMail("deactivated" + System.currentTimeMillis() + "@deactivated.com");
			applicant.setEmployer(null);
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
			applicant.setOpinion(null);

			this.applicantForumRepository.save(applicant);
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
	private List<String> getAllMobilities(final JsonNode jMobilities) {
		final List<String> allMobilities = new ArrayList<String>();
		for (final JsonNode mobility : jMobilities) {
			final Mobility mobilityToFind = new Mobility();
			mobilityToFind.setPlaceName(mobility.get("placeName").textValue());
			mobilityToFind.setPlaceType(mobility.get("placeType").textValue());
			mobilityToFind.setRadius(Integer.parseInt(mobility.get("radius").textValue()));
			mobilityToFind.setUnit(mobility.get("unit").textValue());

			final Mobility optionalMobility = this.mobilityEntityController.getMobility(mobilityToFind)
					.orElse(mobilityToFind);
			allMobilities.add(optionalMobility.get_id().toString());
		}
		return allMobilities;
	}

	/**
	 * @param mail the applicant's mail to fetch
	 * @return the optional with the applicant fetched
	 * @author Andy Chabalier
	 */
	public Optional<ApplicantForum> getApplicantByMail(final String mail) {
		return this.applicantForumRepository.findByMail(mail);
	}

	/**
	 * @return the list of all applicants
	 * @author Andy Chabalier
	 */
	public List<ApplicantForum> getApplicants() {
		return this.applicantForumRepository.findAll();
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
	public HttpException updateApplicant(final JsonNode jApplicant) throws ParseException {
		final Optional<ApplicantForum> optionalApplicant = this.applicantForumRepository
				.findByMail(jApplicant.get("oldMail").textValue());
		if (optionalApplicant.isPresent()) {
			final ApplicantForum applicant = optionalApplicant.get();
			applicant.setName(jApplicant.get("name").textValue());
			applicant.setSurname(jApplicant.get("surname").textValue());
			applicant.setMonthlyWage(Integer.parseInt(jApplicant.get("wage").textValue()));

			applicant.setMail(jApplicant.get("mail").textValue());

			final User personInCharge = this.userEntityController
					.getUserByMail(jApplicant.get("managerMail").textValue());
			applicant.setPersonInChargeMail(personInCharge.getMail());

			final String highestDiploma = jApplicant.get("highestDiploma").textValue();
			final String highestDiplomaYear = jApplicant.get("highestDiplomaYear").textValue();
			Diploma diploma;
			try {
				diploma = this.diplomaEntityController.getDiplomaByNameAndYearOfResult(highestDiploma,
						highestDiplomaYear);
			} catch (final ResourceNotFoundException e) {
				diploma = this.diplomaEntityController.createDiploma(highestDiploma, highestDiplomaYear);
			}

			applicant.setHighestDiploma(diploma.getName());
			applicant.setHighestDiplomaYear(diploma.getYearOfResult());

			final String jobName = jApplicant.get("job").textValue();
			Job job;
			try {
				job = this.jobEntityController.getJob(jobName);
			} catch (final ResourceNotFoundException e) {
				job = this.jobEntityController.createJob(jobName);
			}
			applicant.setJob(job.getTitle());

			final String employerName = jApplicant.get("employer").textValue();
			Employer employer;
			try {
				employer = this.employerEntityController.getEmployer(employerName);
			} catch (final ResourceNotFoundException e) {
				employer = this.employerEntityController.createEmployer(employerName);
			}

			applicant.setEmployer(employer.getName());

			applicant.setMobilities(getAllMobilities(jApplicant.get("mobilities")));
			applicant.setPhoneNumber(jApplicant.get("phoneNumber").textValue());

			applicant.setStartAt(jApplicant.get("canStartAt").textValue());
			applicant.setGrade(jApplicant.get("grade").textValue());
			applicant.setCommentary(jApplicant.get("commentary").textValue());
			applicant.setContractType(jApplicant.get("contractType").textValue());
			applicant.setContractDuration(jApplicant.get("contractDuration").textValue());

			final List<String> skills = new ArrayList<String>();
			final JsonNode skillNode = jApplicant.get("skills");
			for (final JsonNode JSkill : skillNode) {
				final String skillName = JSkill.get("name").textValue();
				Skill skill;

				try {
					skill = this.skillEntityController.getSkill(skillName);
				} catch (final ResourceNotFoundException e) {
					skill = this.skillEntityController.createSkill(skillName, null).get();
				}

				skills.add(skill.getName());
			}
			applicant.setSkills(skills);

			applicant.setOpinion(jApplicant.get("opinion").textValue());

			applicant.setMobilities(getAllMobilities(jApplicant.get("mobilities")));
			applicant.setPhoneNumber(jApplicant.get("phoneNumber").textValue());

			applicant.setStartAt(jApplicant.get("canStartAt").textValue());
			applicant.setGrade(jApplicant.get("grade").textValue());
			applicant.setCommentary(jApplicant.get("commentary").textValue());
			applicant.setContractType(jApplicant.get("contractType").textValue());
			applicant.setContractDuration(jApplicant.get("contractDuration").textValue());

			applicant.setVehicule(Boolean.getBoolean(jApplicant.get("hasVehicule").textValue()));
			applicant.setDriverLicense(Boolean.getBoolean(jApplicant.get("hasPermis").textValue()));
			applicant.setNationality(Nationality.valueOf(jApplicant.get("nationality").textValue()));

			this.applicantForumRepository.save(applicant);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}
}
