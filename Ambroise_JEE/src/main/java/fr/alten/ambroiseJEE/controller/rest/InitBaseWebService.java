/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.AgencyBusinessController;
import fr.alten.ambroiseJEE.controller.business.ApplicantBusinessController;
import fr.alten.ambroiseJEE.controller.business.ApplicantForumBusinessController;
import fr.alten.ambroiseJEE.controller.business.ConsultantBusinessController;
import fr.alten.ambroiseJEE.controller.business.DiplomaBusinessController;
import fr.alten.ambroiseJEE.controller.business.EmployerBusinessController;
import fr.alten.ambroiseJEE.controller.business.ForumBusinessController;
import fr.alten.ambroiseJEE.controller.business.JobBusinessController;
import fr.alten.ambroiseJEE.controller.business.SectorBusinessController;
import fr.alten.ambroiseJEE.controller.business.SkillBusinessController;
import fr.alten.ambroiseJEE.controller.business.SkillsSheetBusinessController;
import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.ApplicantForum;
import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Rest Controller for User Administration !!!! ONLY FOR DEVELOPPMENT !!!!
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class InitBaseWebService {

	@Autowired
	private UserBusinessController userBusinessController;

	@Autowired
	private GeographicBusinessController geographicBusinessController;

	@Autowired
	private AgencyBusinessController AgencyBusinessController;

	@Autowired
	private DiplomaBusinessController diplomaBusinessController;

	@Autowired
	private EmployerBusinessController employerBusinessController;

	@Autowired
	private JobBusinessController jobBusinessController;

	@Autowired
	private ApplicantBusinessController applicantBusinessController;

	@Autowired
	private ConsultantBusinessController consultantBusinessController;

	@Autowired
	private SkillBusinessController skillBusinessController;

	@Autowired
	private SkillsSheetBusinessController skillsSheetBusinessController;

	@Autowired
	private ForumBusinessController forumBusinessController;

	@Autowired
	private SectorBusinessController sectorBusinessController;

	@Autowired
	private ApplicantForumBusinessController applicantForumBusinessController;

	private final Gson gson;

	public InitBaseWebService() {
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	/**
	 * create forum applicant
	 * 
	 * @author Andy Chabalier
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createApplicantForum() throws IOException, ParseException {
		Map<String, String> skills = new HashMap<String, String>();
		for (String skill : Arrays.asList("c++", "java", "c#", "javascript")) {
			skills.put("name", skill);
		}

		// Remplissage d'une population de consultant
		for (int i = 0; i < 10; i++) {
			final ApplicantForum applicantForumi = new ApplicantForum();
			applicantForumi.setMail("applicantForum" + i + "@mail.com");
			applicantForumi.setName("applicantForumName");
			applicantForumi.setSurname("applicantForumSurname");
			applicantForumi.setJob("DevOps");
			applicantForumi.setEmployer("ALTEN");
			applicantForumi.setPhoneNumber("0000000000");
			applicantForumi.setStartAt("11/11/11");
			applicantForumi.setCommentary("Comentary for" + applicantForumi.getName());
			applicantForumi.setContractDuration("infinity");
			applicantForumi.setContractType("CDI");

			final JsonNode applicantForumiJsonNode = JsonUtils
					.toJsonNode(this.gson.toJsonTree(applicantForumi).getAsJsonObject());
			((ObjectNode) applicantForumiJsonNode).put("diplomaName", "MASTER");
			((ObjectNode) applicantForumiJsonNode).put("diplomaYear", "2019");
			((ObjectNode) applicantForumiJsonNode).put("monthlyWage", "" + (i + 1) * 1000);
			((ObjectNode) applicantForumiJsonNode).put("managerMail", "tempUserAdminManager@mail.com");
			((ObjectNode) applicantForumiJsonNode).put("canStartAt", "11/11/11");
			((ObjectNode) applicantForumiJsonNode).put("skills", JsonUtils.toJsonNode(skills).asText());
			((ObjectNode) applicantForumiJsonNode).putNull("mobilities");
			((ObjectNode) applicantForumiJsonNode).putNull("docs");
			((ObjectNode) applicantForumiJsonNode).putNull("grade");
			((ObjectNode) applicantForumiJsonNode).putNull("hasVehicule");
			((ObjectNode) applicantForumiJsonNode).putNull("hasPermis");
			((ObjectNode) applicantForumiJsonNode).putNull("nationality");

			this.applicantForumBusinessController.createApplicant(applicantForumiJsonNode, UserRole.MANAGER_ADMIN);
		}
	}

	/**
	 *
	 * @author Andy Chabalier
	 * @throws IOException if an I/O problem occur
	 */
	private void createAgencies() throws IOException {
		final Agency agencyStrasbourg = new Agency();
		agencyStrasbourg.setName("Strasbourg");
		final JsonNode agencyStrasbourgJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyStrasbourg).getAsJsonObject());
		((ObjectNode) agencyStrasbourgJsonNode).put("place", "Schiltigheim");
		((ObjectNode) agencyStrasbourgJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyStrasbourgJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyMulhouse = new Agency();
		agencyMulhouse.setName("Mulhouse");
		final JsonNode agencyMulhouseJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyMulhouse).getAsJsonObject());
		((ObjectNode) agencyMulhouseJsonNode).put("place", "Mulhouse");
		((ObjectNode) agencyMulhouseJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyMulhouseJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyLille = new Agency();
		agencyLille.setName("Lille");
		final JsonNode agencyLilleJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(agencyLille).getAsJsonObject());
		((ObjectNode) agencyLilleJsonNode).put("place", "Villeneuve-d'Ascq");
		((ObjectNode) agencyLilleJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyLilleJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyParis = new Agency();
		agencyParis.setName("Paris");
		final JsonNode agencyParisJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(agencyParis).getAsJsonObject());
		((ObjectNode) agencyParisJsonNode).put("place", "Boulogne-Billancourt");
		((ObjectNode) agencyParisJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyParisJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyRennes = new Agency();
		agencyRennes.setName("Rennes");
		final JsonNode agencyRennesJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyRennes).getAsJsonObject());
		((ObjectNode) agencyRennesJsonNode).put("place", "Rennes");
		((ObjectNode) agencyRennesJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyRennesJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyNantes = new Agency();
		agencyNantes.setName("Nantes");
		final JsonNode agencyNantesJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyNantes).getAsJsonObject());
		((ObjectNode) agencyNantesJsonNode).put("place", "Saint-Herblain");
		((ObjectNode) agencyNantesJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyNantesJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyBordeaux = new Agency();
		agencyBordeaux.setName("Bordeaux");
		final JsonNode agencyBordeauxJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyBordeaux).getAsJsonObject());
		((ObjectNode) agencyBordeauxJsonNode).put("place", "Merignac");
		((ObjectNode) agencyBordeauxJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyBordeauxJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyLyon = new Agency();
		agencyLyon.setName("Lyon");
		final JsonNode agencyLyonJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(agencyLyon).getAsJsonObject());
		((ObjectNode) agencyLyonJsonNode).put("place", "Villeurbanne");
		((ObjectNode) agencyLyonJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyLyonJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyGrenoble = new Agency();
		agencyGrenoble.setName("Grenoble");
		final JsonNode agencyGrenobleJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyGrenoble).getAsJsonObject());
		((ObjectNode) agencyGrenobleJsonNode).put("place", "Grenoble");
		((ObjectNode) agencyGrenobleJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyGrenobleJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyToulouse = new Agency();
		agencyToulouse.setName("Toulouse");
		final JsonNode agencyToulouseJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyToulouse).getAsJsonObject());
		((ObjectNode) agencyToulouseJsonNode).put("place", "Labège");
		((ObjectNode) agencyToulouseJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyToulouseJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyAixEnProvence = new Agency();
		agencyAixEnProvence.setName("Aix-en-Provence");
		final JsonNode agencyAixEnProvenceJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(agencyAixEnProvence).getAsJsonObject());
		((ObjectNode) agencyAixEnProvenceJsonNode).put("place", "Aix-en-Provence");
		((ObjectNode) agencyAixEnProvenceJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyAixEnProvenceJsonNode, UserRole.MANAGER_ADMIN);

		final Agency agencyNice = new Agency();
		agencyNice.setName("Nice");
		final JsonNode agencyNiceJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(agencyNice).getAsJsonObject());
		((ObjectNode) agencyNiceJsonNode).put("place", "Valbonne");
		((ObjectNode) agencyNiceJsonNode).put("placeType", "city");
		this.AgencyBusinessController.createAgency(agencyNiceJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate Database with diplomas
	 *
	 * @author Lucas Royackkers
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createDiplomas() throws IOException, ParseException {
		final Diploma diplomaEpitech = new Diploma();
		diplomaEpitech.setName("EPITECH");
		diplomaEpitech.setYearOfResult("2019");
		final JsonNode diplomaEpitechJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(diplomaEpitech).getAsJsonObject());
		this.diplomaBusinessController.createDiploma(diplomaEpitechJsonNode, UserRole.MANAGER_ADMIN);

		final Diploma diplomaEnsisa = new Diploma();
		diplomaEnsisa.setName("ENSISA");
		diplomaEnsisa.setYearOfResult("2019");
		final JsonNode diplomaEnsisaJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(diplomaEnsisa).getAsJsonObject());
		this.diplomaBusinessController.createDiploma(diplomaEnsisaJsonNode, UserRole.MANAGER_ADMIN);

		final Diploma diplomaTelNancy = new Diploma();
		diplomaTelNancy.setName("Telecom Nancy");
		diplomaTelNancy.setYearOfResult("2019");
		final JsonNode diplomaTelNancyJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(diplomaTelNancy).getAsJsonObject());
		this.diplomaBusinessController.createDiploma(diplomaTelNancyJsonNode, UserRole.MANAGER_ADMIN);

		final Diploma diplomaEnsiie = new Diploma();
		diplomaEnsiie.setName("ENSIIE");
		diplomaEnsiie.setYearOfResult("2019");
		final JsonNode diplomaEnsiieJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(diplomaEnsiie).getAsJsonObject());
		this.diplomaBusinessController.createDiploma(diplomaEnsiieJsonNode, UserRole.MANAGER_ADMIN);

		final Diploma diplomaEiCesi = new Diploma();
		diplomaEiCesi.setName("EiCESI");
		diplomaEiCesi.setYearOfResult("2019");
		final JsonNode diplomaEiCesiJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(diplomaEiCesi).getAsJsonObject());
		this.diplomaBusinessController.createDiploma(diplomaEiCesiJsonNode, UserRole.MANAGER_ADMIN);

		final Diploma diplomaMaster = new Diploma();
		diplomaMaster.setName("MASTER");
		diplomaMaster.setYearOfResult("2019");
		final JsonNode diplomaMasterJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(diplomaMaster).getAsJsonObject());
		this.diplomaBusinessController.createDiploma(diplomaMasterJsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate Database with Employers
	 *
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	private void createEmployers() throws IOException {
		final Employer bouygues = new Employer();
		bouygues.setName("Bouygues");
		final JsonNode bouyguesJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(bouygues).getAsJsonObject());
		this.employerBusinessController.createEmployer(bouyguesJsonNode, UserRole.MANAGER_ADMIN);

		final Employer sopra = new Employer();
		sopra.setName("Sopra");
		final JsonNode sopraJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(sopra).getAsJsonObject());
		this.employerBusinessController.createEmployer(sopraJsonNode, UserRole.MANAGER_ADMIN);

		final Employer alstom = new Employer();
		alstom.setName("Alstom");
		final JsonNode alstomJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(alstom).getAsJsonObject());
		this.employerBusinessController.createEmployer(alstomJsonNode, UserRole.MANAGER_ADMIN);

		final Employer capgemini = new Employer();
		capgemini.setName("Capgemini");
		final JsonNode capgeminiJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(capgemini).getAsJsonObject());
		this.employerBusinessController.createEmployer(capgeminiJsonNode, UserRole.MANAGER_ADMIN);

		final Employer ge = new Employer();
		ge.setName("G.E.");
		final JsonNode geJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(ge).getAsJsonObject());
		this.employerBusinessController.createEmployer(geJsonNode, UserRole.MANAGER_ADMIN);

		final Employer alten = new Employer();
		alten.setName("ALTEN");
		final JsonNode altenJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(alten).getAsJsonObject());
		this.employerBusinessController.createEmployer(altenJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate database with forums
	 *
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createForums() throws IOException {
		final Forum newForum = new Forum();
		newForum.setName("EPITECH");
		newForum.setDate("2019");
		newForum.setPlace("Nancy");
		final JsonNode newForumJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newForum).getAsJsonObject());
		this.forumBusinessController.createForum(newForumJsonNode, UserRole.MANAGER_ADMIN);

		final Forum newForum1 = new Forum();
		newForum1.setName("AlsaTech");
		newForum1.setDate("2019");
		newForum1.setPlace("Strasbourg");
		final JsonNode newForum1JsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newForum1).getAsJsonObject());
		this.forumBusinessController.createForum(newForum1JsonNode, UserRole.MANAGER_ADMIN);

		final Forum newForum2 = new Forum();
		newForum2.setName("ENSISA");
		newForum2.setDate("2019");
		newForum2.setPlace("Schiltigheim");
		final JsonNode newForum2JsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newForum2).getAsJsonObject());
		this.forumBusinessController.createForum(newForum2JsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * create and populate Database with geographics
	 *
	 * @author Andy Chabalier
	 */
	private void createGeographics() {
		this.geographicBusinessController.synchronize(UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate Database with Jobs
	 *
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	private void createJobs() throws IOException {
		final Job ingenieurSystem = new Job();
		ingenieurSystem.setTitle("Ingénieur système");
		final JsonNode ingenieurSystemJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(ingenieurSystem).getAsJsonObject());
		this.jobBusinessController.createJob(ingenieurSystemJsonNode, UserRole.MANAGER_ADMIN);

		final Job ingenieurWeb = new Job();
		ingenieurWeb.setTitle("Ingénieur WEB");
		final JsonNode ingenieurWebJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(ingenieurWeb).getAsJsonObject());
		this.jobBusinessController.createJob(ingenieurWebJsonNode, UserRole.MANAGER_ADMIN);

		final Job ingenieurReseau = new Job();
		ingenieurReseau.setTitle("Ingénieur réseau");
		final JsonNode ingenieurReseauJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(ingenieurReseau).getAsJsonObject());
		this.jobBusinessController.createJob(ingenieurReseauJsonNode, UserRole.MANAGER_ADMIN);

		final Job webDesigner = new Job();
		webDesigner.setTitle("Web Designer");
		final JsonNode webDesignerJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(webDesigner).getAsJsonObject());
		this.jobBusinessController.createJob(webDesignerJsonNode, UserRole.MANAGER_ADMIN);

		final Job chefDeProjetIT = new Job();
		chefDeProjetIT.setTitle("Chef de projet IT");
		final JsonNode chefDeProjetITJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(chefDeProjetIT).getAsJsonObject());
		this.jobBusinessController.createJob(chefDeProjetITJsonNode, UserRole.MANAGER_ADMIN);

		final Job devOps = new Job();
		devOps.setTitle("DevOps");
		final JsonNode devOpsJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(devOps).getAsJsonObject());
		this.jobBusinessController.createJob(devOpsJsonNode, UserRole.MANAGER_ADMIN);

		final Job devFullStack = new Job();
		devFullStack.setTitle("Développeur full stack");
		final JsonNode devFullStackJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(devFullStack).getAsJsonObject());
		this.jobBusinessController.createJob(devFullStackJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate Database with persons
	 *
	 * @author Lucas Royackkers
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createPersons() throws IOException, ParseException {
		final Person newCandidatMichel = new Person();
		newCandidatMichel.setSurname("Michel");
		newCandidatMichel.setName("Test");
		newCandidatMichel.setMail("michel.test@gmail.com");
		newCandidatMichel.setEmployer("Sopra");
		newCandidatMichel.setJob("Développeur full stack");
		newCandidatMichel.setHighestDiploma("EiCESI");
		newCandidatMichel.setHighestDiplomaYear("2019");
		newCandidatMichel.setPersonInChargeMail("tempUserAdminManager@mail.com");

		final JsonNode newCandidatMichelJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(newCandidatMichel).getAsJsonObject());
		((ObjectNode) newCandidatMichelJsonNode).put("monthlyWage", "2525");
		((ObjectNode) newCandidatMichelJsonNode).putNull("urlDocs");
		this.applicantBusinessController.createApplicant(newCandidatMichelJsonNode, UserRole.MANAGER_ADMIN);

		final Person newCandidatPaul = new Person();
		newCandidatPaul.setSurname("Paul");
		newCandidatPaul.setName("Test");
		newCandidatPaul.setMail("paul.test@gmail.com");
		newCandidatPaul.setEmployer("Alstom");
		newCandidatPaul.setJob("Ingénieur système");
		newCandidatPaul.setHighestDiploma("MASTER");
		newCandidatPaul.setHighestDiplomaYear("2019");
		newCandidatPaul.setRole(PersonRole.APPLICANT);
		newCandidatPaul.setPersonInChargeMail("tempUserAdminManager@mail.com");

		final JsonNode newCandidatPaulJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(newCandidatPaul).getAsJsonObject());
		((ObjectNode) newCandidatPaulJsonNode).put("monthlyWage", "2150");
		((ObjectNode) newCandidatPaulJsonNode).putNull("urlDocs");
		this.applicantBusinessController.createApplicant(newCandidatPaulJsonNode, UserRole.MANAGER_ADMIN);

		final Person newCandidatCyprien = new Person();
		newCandidatCyprien.setSurname("Cyprien");
		newCandidatCyprien.setName("Test");
		newCandidatCyprien.setMail("cyprien.test@gmail.com");
		newCandidatCyprien.setEmployer("ALTEN");
		newCandidatCyprien.setJob("Chef de projet IT");
		newCandidatCyprien.setHighestDiploma("ENSISA");
		newCandidatCyprien.setHighestDiplomaYear("2019");
		newCandidatCyprien.setRole(PersonRole.CONSULTANT);
		newCandidatCyprien.setPersonInChargeMail("tempUserAdminManager@mail.com");

		final JsonNode newCandidatCyprienJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(newCandidatCyprien).getAsJsonObject());
		((ObjectNode) newCandidatCyprienJsonNode).put("monthlyWage", "4525");
		((ObjectNode) newCandidatCyprienJsonNode).putNull("urlDocs");
		this.consultantBusinessController.createConsultant(newCandidatCyprienJsonNode, UserRole.MANAGER_ADMIN);

		final Person newCandidatJeanClaude = new Person();
		newCandidatJeanClaude.setSurname("Jean-Claude");
		newCandidatJeanClaude.setName("Test");
		newCandidatJeanClaude.setMail("jc.test@gmail.com");
		newCandidatJeanClaude.setEmployer("ALTEN");
		newCandidatJeanClaude.setJob("DevOps");
		newCandidatJeanClaude.setHighestDiploma("EPITECH");
		newCandidatJeanClaude.setHighestDiplomaYear("2019");
		newCandidatJeanClaude.setRole(PersonRole.CONSULTANT);
		newCandidatJeanClaude.setPersonInChargeMail("tempUserAdminManager@mail.com");

		final JsonNode newCandidatJeanClaudeJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(newCandidatJeanClaude).getAsJsonObject());
		((ObjectNode) newCandidatJeanClaudeJsonNode).put("monthlyWage", "3215");
		((ObjectNode) newCandidatJeanClaudeJsonNode).putNull("urlDocs");
		this.consultantBusinessController.createConsultant(newCandidatJeanClaudeJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate database with sectors
	 *
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createSectors() throws IOException {
		final Sector newSector = new Sector();
		newSector.setName("Aéronautique");
		final JsonNode newSectorJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newSector).getAsJsonObject());
		this.sectorBusinessController.createSector(newSectorJsonNode, UserRole.MANAGER_ADMIN);

		final Sector newSector1 = new Sector();
		newSector1.setName("Banque");
		final JsonNode newSector1JsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newSector1).getAsJsonObject());
		this.sectorBusinessController.createSector(newSector1JsonNode, UserRole.MANAGER_ADMIN);

		final Sector newSector2 = new Sector();
		newSector2.setName("Grande distribution");
		final JsonNode newSector2JsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newSector2).getAsJsonObject());
		this.sectorBusinessController.createSector(newSector2JsonNode, UserRole.MANAGER_ADMIN);

		final Sector newSector3 = new Sector();
		newSector3.setName("Ferroviaire");
		final JsonNode newSector3JsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newSector3).getAsJsonObject());
		this.sectorBusinessController.createSector(newSector3JsonNode, UserRole.MANAGER_ADMIN);

		final Sector newSector4 = new Sector();
		newSector4.setName("Automobile");
		final JsonNode newSector4JsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newSector4).getAsJsonObject());
		this.sectorBusinessController.createSector(newSector4JsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate database with skills sheets.
	 *
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createSkillsSheets() throws IOException {
		final SkillsSheet newFirst = new SkillsSheet();
		newFirst.setName("JTE-mmm-AAA");
		newFirst.setMailPersonAttachedTo("jc.test@gmail.com");
		newFirst.setRolePersonAttachedTo("consultant");
		newFirst.setMailVersionAuthor("tempUserAdminManager@mail.com");

		final List<Skill> newListSkill = new ArrayList<Skill>();
		
		final Skill newSoftSkill = new Skill();
		newSoftSkill.setName("Cycle en V");
		newSoftSkill.setGrade(2);
		newSoftSkill.setIsSoft("true");

		final Skill newSoftSkill1 = new Skill();
		newSoftSkill1.setName("Gestion de projet");
		newSoftSkill1.setGrade(3);
		newSoftSkill1.setIsSoft("true");

		final Skill newSoftSkill2 = new Skill();
		newSoftSkill2.setName("Communication");
		newSoftSkill2.setGrade(2);
		newSoftSkill2.setIsSoft("true");

		final Skill newSkill = new Skill();
		newSkill.setName("JavaScript");
		newSkill.setGrade(4);
		newSkill.setIsSoft(null);

		final Skill newSkill1 = new Skill();
		newSkill1.setName("Angular");
		newSkill1.setGrade(2);
		newSkill1.setIsSoft(null);

		final Skill newSkill2 = new Skill();
		newSkill2.setName("C++");
		newSkill2.setGrade(2.5);
		newSkill2.setIsSoft(null);

		newListSkill.add(newSoftSkill);
		newListSkill.add(newSoftSkill1);
		newListSkill.add(newSoftSkill2);

		newListSkill.add(newSkill);
		newListSkill.add(newSkill1);
		newListSkill.add(newSkill2);

		newFirst.setSkillsList(newListSkill);

		final JsonNode newFirstJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newFirst).getAsJsonObject());
		((ObjectNode) newFirstJsonNode).put("role", "consultant");
		this.skillsSheetBusinessController.createSkillsSheet(newFirstJsonNode, UserRole.MANAGER_ADMIN);

		final SkillsSheet newSecond = new SkillsSheet();
		newSecond.setName("PTE-mmm-AAA");
		newSecond.setMailPersonAttachedTo("paul.test@gmail.com");
		newSecond.setRolePersonAttachedTo("applicant");
		newSecond.setMailVersionAuthor("tempUserAdminManager@mail.com");

		final List<Skill> newListSkillBis = new ArrayList<Skill>();

		final Skill newSoftSkillBis = new Skill();
		newSoftSkillBis.setName("Cycle en V");
		newSoftSkillBis.setGrade(3);
		newSoftSkillBis.setIsSoft("true");

		final Skill newSoftSkill1Bis = new Skill();
		newSoftSkill1Bis.setName("Gestion de projet");
		newSoftSkill1Bis.setGrade(1.5);
		newSoftSkill1Bis.setIsSoft("true");
		
		final Skill newSoftSkill2Bis = new Skill();
		newSoftSkill2Bis.setName("Communication");
		newSoftSkill2Bis.setGrade(3.5);
		newSoftSkill2Bis.setIsSoft("true");

		final Skill newSkillBis = new Skill();
		newSkillBis.setName("C#");
		newSkillBis.setGrade(4);
		newSkillBis.setIsSoft(null);

		final Skill newSkill1Bis = new Skill();
		newSkill1Bis.setName("Angular");
		newSkill1Bis.setGrade(2);
		newSkill1Bis.setIsSoft(null);

		final Skill newSkill2Bis = new Skill();
		newSkill2Bis.setName("C++");
		newSkill2Bis.setGrade(3);
		newSkill2Bis.setIsSoft(null);

		newListSkillBis.add(newSoftSkillBis);
		newListSkillBis.add(newSoftSkill1Bis);
		newListSkillBis.add(newSoftSkill2Bis);

		newListSkillBis.add(newSkillBis);
		newListSkillBis.add(newSkill1Bis);
		newListSkillBis.add(newSkill2Bis);

		newSecond.setSkillsList(newListSkillBis);

		final JsonNode newSecondJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newSecond).getAsJsonObject());
		((ObjectNode) newSecondJsonNode).put("role", "applicant");
		this.skillsSheetBusinessController.createSkillsSheet(newSecondJsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate Database with Skills
	 *
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createSkills() throws IOException {
		final Skill newCpp = new Skill();
		newCpp.setName("C++");
		newCpp.setIsSoft(null);
		final JsonNode newCppJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newCpp).getAsJsonObject());
		this.skillBusinessController.createSkill(newCppJsonNode, UserRole.MANAGER_ADMIN);

		final Skill newAngular = new Skill();
		newAngular.setName("Angular");
		newAngular.setIsSoft(null);
		final JsonNode newAngularJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newAngular).getAsJsonObject());
		this.skillBusinessController.createSkill(newAngularJsonNode, UserRole.MANAGER_ADMIN);

		final Skill newCSharp = new Skill();
		newCSharp.setName("C#");
		newCSharp.setIsSoft(null);
		final JsonNode newCSharpJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newCSharp).getAsJsonObject());
		this.skillBusinessController.createSkill(newCSharpJsonNode, UserRole.MANAGER_ADMIN);

		final Skill newJS = new Skill();
		newJS.setName("JavaScript");
		newJS.setIsSoft(null);
		final JsonNode newJSJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newJS).getAsJsonObject());
		this.skillBusinessController.createSkill(newJSJsonNode, UserRole.MANAGER_ADMIN);

		final Skill newCycle = new Skill();
		newCycle.setName("Cycle en V");
		newCycle.setIsSoft("true");
		final JsonNode newCycleJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newCycle).getAsJsonObject());
		this.skillBusinessController.createSkill(newCycleJsonNode, UserRole.MANAGER_ADMIN);
		
		
		final Skill newProjet = new Skill();
		newProjet.setName("Gestion de projet");
		newProjet.setIsSoft("true");
		final JsonNode newProjetJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newProjet).getAsJsonObject());
		this.skillBusinessController.createSkill(newProjetJsonNode, UserRole.MANAGER_ADMIN);
		
		final Skill newCommunication = new Skill();
		newCommunication.setName("Communication");
		newCommunication.setIsSoft("true");
		final JsonNode newCommunicationJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(newCommunication).getAsJsonObject());
		this.skillBusinessController.createSkill(newCommunicationJsonNode, UserRole.MANAGER_ADMIN);
		
	}

	/**
	 * create and populate Database with users 1 user with Manager_Admin role, 1
	 * user with CDR_Admin role, 1 user with Manager role, 1 user with CDR role,, 1
	 * user with Consultant role, 1 user who will be deactivated and 10 consultants
	 *
	 * @throws IOException
	 * @author Andy Chabalier
	 */
	private void createUsers() throws IOException {

		final User userAdminManager = new User();
		userAdminManager.setForname("tempUserAdminManager");
		userAdminManager.setMail("tempUserAdminManager@mail.com");
		userAdminManager.setName("tempUserAdminManagerName");

		final User userAdminCDR = new User();
		userAdminCDR.setForname("tempUserAdminCDR");
		userAdminCDR.setMail("tempUserAdminCDR@mail.com");
		userAdminCDR.setName("tempUserAdminCDRName");

		final User userManager = new User();
		userManager.setForname("tempUserManager");
		userManager.setMail("tempUserManager@mail.com");
		userManager.setName("tempUserManagerName");

		final User userCDR = new User();
		userCDR.setForname("tempUserCDR");
		userCDR.setMail("tempUserCDR@mail.com");
		userCDR.setName("tempUserCDRName");

		final User userConsultant = new User();
		userConsultant.setForname("tempUserConsultant");
		userConsultant.setMail("tempUserConsultant@mail.com");
		userConsultant.setName("tempUserConsultantName");

		final User userDeactivated = new User();
		userDeactivated.setForname("");
		userDeactivated.setMail("deactivated" + System.currentTimeMillis());
		userDeactivated.setName("");

		final JsonNode userAdminManagerJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(userAdminManager).getAsJsonObject());
		((ObjectNode) userAdminManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminManagerJsonNode).put("role", "MANAGER_ADMIN");
		((ObjectNode) userAdminManagerJsonNode).put("agency", "Strasbourg");
		final JsonNode userAdminCDRJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(userAdminCDR).getAsJsonObject());
		((ObjectNode) userAdminCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminCDRJsonNode).put("role", "CDR_ADMIN");
		((ObjectNode) userAdminCDRJsonNode).put("agency", "Strasbourg");
		final JsonNode userManagerJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(userManager).getAsJsonObject());
		((ObjectNode) userManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userManagerJsonNode).put("role", "MANAGER");
		((ObjectNode) userManagerJsonNode).put("agency", "Strasbourg");
		final JsonNode userCDRJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(userCDR).getAsJsonObject());
		((ObjectNode) userCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userCDRJsonNode).put("role", "CDR");
		((ObjectNode) userCDRJsonNode).put("agency", "Strasbourg");
		final JsonNode userConsultantJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(userConsultant).getAsJsonObject());
		((ObjectNode) userConsultantJsonNode).put("pswd", "pass");
		((ObjectNode) userConsultantJsonNode).put("role", "CONSULTANT");
		((ObjectNode) userConsultantJsonNode).put("agency", "Strasbourg");
		final JsonNode userDeactivatedJsonNode = JsonUtils
				.toJsonNode(this.gson.toJsonTree(userDeactivated).getAsJsonObject());
		((ObjectNode) userDeactivatedJsonNode).put("pswd", "pass");
		((ObjectNode) userDeactivatedJsonNode).put("role", "DEACTIVATED");
		((ObjectNode) userDeactivatedJsonNode).put("agency", "Strasbourg");

		this.userBusinessController.createUser(userAdminManagerJsonNode, UserRole.MANAGER_ADMIN);
		this.userBusinessController.createUser(userAdminCDRJsonNode, UserRole.MANAGER_ADMIN);
		this.userBusinessController.createUser(userManagerJsonNode, UserRole.MANAGER_ADMIN);
		this.userBusinessController.createUser(userCDRJsonNode, UserRole.MANAGER_ADMIN);
		this.userBusinessController.createUser(userConsultantJsonNode, UserRole.MANAGER_ADMIN);
		this.userBusinessController.createUser(userDeactivatedJsonNode, UserRole.MANAGER_ADMIN);

		// Remplissage d'une population de consultant
		for (int i = 0; i < 10; i++) {
			final User useri = new User();
			useri.setForname("tempUserConsultant" + i);
			useri.setMail("tempUserconsultant" + i + "@mail.com");
			useri.setName("tempUserAdminName");

			final JsonNode useriJsonNode = JsonUtils.toJsonNode(this.gson.toJsonTree(useri).getAsJsonObject());
			((ObjectNode) useriJsonNode).put("pswd", "pass");
			((ObjectNode) useriJsonNode).put("role", "CONSULTANT");
			((ObjectNode) useriJsonNode).put("agency", "Strasbourg");

			this.userBusinessController.createUser(useriJsonNode, UserRole.MANAGER_ADMIN);
		}
	}

	/**
	 * Method to populate the database. HTTP Method : POST ATTENTION ! This will
	 * populate the database entirely with a preset of datas, thus check if you have
	 * a Mongo Repository running in your PC, and check the result in it
	 *
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if all datas are created
	 * @throws IOException
	 * @throws ParseException
	 * @author Lucas Royackkers, Andy Chabalier
	 */
	@PostMapping(value = "/admin/init")
	@ResponseBody
	public HttpException init() throws IOException, ParseException {
		long start = System.currentTimeMillis();
		// peupler la base de données des diplômes
		createDiplomas();

		// peupler la base de données des employeurs
		createEmployers();

		// peupler la base de données des métiers consultants
		createJobs();

		// peupler la base de données géographiques
		createGeographics();

		// peupler la base de données forums
		createForums();

		// peupler la base de données secteud d'activité
		createSectors();

		// peupler la base de données des compétences (Skill)
		createSkills();

		// peupler la base de données Géographiques
		createAgencies();

		// peupler la base d'utilisateurs
		createUsers();

		// peupler la base de données des personnes (identités consultants, candidats)
		createPersons();

		// peupler la base de données des matrices de compétences
		createSkillsSheets();

		// peupler la base de données des candidats sur un forum
		createApplicantForum();

		LoggerFactory.getLogger(InitBaseWebService.class).info(String.valueOf(System.currentTimeMillis() - start));
		return new CreatedException();
	}
}