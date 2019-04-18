/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import fr.alten.ambroiseJEE.controller.business.ConsultantBusinessController;
import fr.alten.ambroiseJEE.controller.business.DiplomaBusinessController;
import fr.alten.ambroiseJEE.controller.business.EmployerBusinessController;
import fr.alten.ambroiseJEE.controller.business.ForumBusinessController;
import fr.alten.ambroiseJEE.controller.business.JobBusinessController;
import fr.alten.ambroiseJEE.controller.business.SectorBusinessController;
import fr.alten.ambroiseJEE.controller.business.SkillsSheetBusinessController;
import fr.alten.ambroiseJEE.controller.business.SoftSkillBusinessController;
import fr.alten.ambroiseJEE.controller.business.TechSkillBusinessController;
import fr.alten.ambroiseJEE.controller.business.UserBusinessController;
import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.beans.SkillsSheet;
import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.beans.User;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.SoftSkillGrade;
import fr.alten.ambroiseJEE.utils.TechSkillGrade;
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
	private TechSkillBusinessController techSkillBusinessController;

	@Autowired
	private SoftSkillBusinessController softSkillBusinessController;

	@Autowired
	private SkillsSheetBusinessController skillsSheetBusinessController;

	@Autowired
	private ForumBusinessController forumBusinessController;

	@Autowired
	private SectorBusinessController sectorBusinessController;

	private final Gson gson;

	public InitBaseWebService() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
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

		// peupler la base de données des compétences techniques (TechSkill)
		createTechSkills();

		// peupler la base de données des compétences soft (SoftSkill)
		createSoftSkills();

		// peupler la base de données Géographiques
		createAgencies();

		// peupler la base d'utilisateurs
		createUsers();

		// peupler la base de données des personnes (identités consultants, candidats)
		createPersons();

		// peupler la base de données des matrices de compétences
		createSkillsSheets();

		return new CreatedException();
	}

	/**
	 * Create and populate Database with diplomas
	 * 
	 * @author Lucas Royackkers
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createDiplomas() throws IOException, ParseException {
		Diploma diplomaEpitech = new Diploma();
		diplomaEpitech.setName("EPITECH");
		diplomaEpitech.setYearOfResult("2019");
		JsonNode diplomaEpitechJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(diplomaEpitech).getAsJsonObject());
		diplomaBusinessController.createDiploma(diplomaEpitechJsonNode, UserRole.MANAGER_ADMIN);

		Diploma diplomaEnsisa = new Diploma();
		diplomaEnsisa.setName("ENSISA");
		diplomaEnsisa.setYearOfResult("2019");
		JsonNode diplomaEnsisaJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(diplomaEnsisa).getAsJsonObject());
		diplomaBusinessController.createDiploma(diplomaEnsisaJsonNode, UserRole.MANAGER_ADMIN);

		Diploma diplomaTelNancy = new Diploma();
		diplomaTelNancy.setName("Telecom Nancy");
		diplomaTelNancy.setYearOfResult("2019");
		JsonNode diplomaTelNancyJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(diplomaTelNancy).getAsJsonObject());
		diplomaBusinessController.createDiploma(diplomaTelNancyJsonNode, UserRole.MANAGER_ADMIN);

		Diploma diplomaEnsiie = new Diploma();
		diplomaEnsiie.setName("ENSIIE");
		diplomaEnsiie.setYearOfResult("2019");
		JsonNode diplomaEnsiieJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(diplomaEnsiie).getAsJsonObject());
		diplomaBusinessController.createDiploma(diplomaEnsiieJsonNode, UserRole.MANAGER_ADMIN);

		Diploma diplomaEiCesi = new Diploma();
		diplomaEiCesi.setName("EiCESI");
		diplomaEiCesi.setYearOfResult("2019");
		JsonNode diplomaEiCesiJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(diplomaEiCesi).getAsJsonObject());
		diplomaBusinessController.createDiploma(diplomaEiCesiJsonNode, UserRole.MANAGER_ADMIN);

		Diploma diplomaMaster = new Diploma();
		diplomaMaster.setName("MASTER");
		diplomaMaster.setYearOfResult("2019");
		JsonNode diplomaMasterJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(diplomaMaster).getAsJsonObject());
		diplomaBusinessController.createDiploma(diplomaMasterJsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate Database with Employers
	 * 
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	private void createEmployers() throws IOException {
		Employer bouygues = new Employer();
		bouygues.setName("Bouygues");
		JsonNode bouyguesJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(bouygues).getAsJsonObject());
		employerBusinessController.createEmployer(bouyguesJsonNode, UserRole.MANAGER_ADMIN);

		Employer sopra = new Employer();
		sopra.setName("Sopra");
		JsonNode sopraJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(sopra).getAsJsonObject());
		employerBusinessController.createEmployer(sopraJsonNode, UserRole.MANAGER_ADMIN);

		Employer alstom = new Employer();
		alstom.setName("Alstom");
		JsonNode alstomJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(alstom).getAsJsonObject());
		employerBusinessController.createEmployer(alstomJsonNode, UserRole.MANAGER_ADMIN);

		Employer capgemini = new Employer();
		capgemini.setName("Capgemini");
		JsonNode capgeminiJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(capgemini).getAsJsonObject());
		employerBusinessController.createEmployer(capgeminiJsonNode, UserRole.MANAGER_ADMIN);

		Employer ge = new Employer();
		ge.setName("G.E.");
		JsonNode geJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(ge).getAsJsonObject());
		employerBusinessController.createEmployer(geJsonNode, UserRole.MANAGER_ADMIN);

		Employer alten = new Employer();
		alten.setName("ALTEN");
		JsonNode altenJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(alten).getAsJsonObject());
		employerBusinessController.createEmployer(altenJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate Database with Jobs
	 * 
	 * @author Lucas Royackkers
	 * @throws IOException
	 */
	private void createJobs() throws IOException {
		Job ingenieurSystem = new Job();
		ingenieurSystem.setTitle("Ingénieur système");
		JsonNode ingenieurSystemJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(ingenieurSystem).getAsJsonObject());
		jobBusinessController.createJob(ingenieurSystemJsonNode, UserRole.MANAGER_ADMIN);

		Job ingenieurWeb = new Job();
		ingenieurWeb.setTitle("Ingénieur WEB");
		JsonNode ingenieurWebJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(ingenieurWeb).getAsJsonObject());
		jobBusinessController.createJob(ingenieurWebJsonNode, UserRole.MANAGER_ADMIN);

		Job ingenieurReseau = new Job();
		ingenieurReseau.setTitle("Ingénieur réseau");
		JsonNode ingenieurReseauJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(ingenieurReseau).getAsJsonObject());
		jobBusinessController.createJob(ingenieurReseauJsonNode, UserRole.MANAGER_ADMIN);

		Job webDesigner = new Job();
		webDesigner.setTitle("Web Designer");
		JsonNode webDesignerJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(webDesigner).getAsJsonObject());
		jobBusinessController.createJob(webDesignerJsonNode, UserRole.MANAGER_ADMIN);

		Job chefDeProjetIT = new Job();
		chefDeProjetIT.setTitle("Chef de projet IT");
		JsonNode chefDeProjetITJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(chefDeProjetIT).getAsJsonObject());
		jobBusinessController.createJob(chefDeProjetITJsonNode, UserRole.MANAGER_ADMIN);

		Job devOps = new Job();
		devOps.setTitle("DevOps");
		JsonNode devOpsJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(devOps).getAsJsonObject());
		jobBusinessController.createJob(devOpsJsonNode, UserRole.MANAGER_ADMIN);

		Job devFullStack = new Job();
		devFullStack.setTitle("Développeur full stack");
		JsonNode devFullStackJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(devFullStack).getAsJsonObject());
		jobBusinessController.createJob(devFullStackJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate Database with persons
	 * 
	 * @author Lucas Royackkers
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createPersons() throws IOException, ParseException {
		Person newCandidatMichel = new Person();
		newCandidatMichel.setSurname("Michel");
		newCandidatMichel.setName("Test");
		newCandidatMichel.setMail("michel.test@gmail.com");
		newCandidatMichel.setEmployer("Sopra");
		newCandidatMichel.setJob("Développeur full stack");
		newCandidatMichel.setHighestDiploma("EiCESI");
		newCandidatMichel.setHighestDiplomaYear("2019");
		newCandidatMichel.setPersonInChargeMail("tempUserAdminManager@mail.com");

		JsonNode newCandidatMichelJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newCandidatMichel).getAsJsonObject());
		((ObjectNode) newCandidatMichelJsonNode).put("monthlyWage", "2525");
		((ObjectNode) newCandidatMichelJsonNode).putNull("urlDocs");
		applicantBusinessController.createApplicant(newCandidatMichelJsonNode, UserRole.MANAGER_ADMIN);

		Person newCandidatPaul = new Person();
		newCandidatPaul.setSurname("Paul");
		newCandidatPaul.setName("Test");
		newCandidatPaul.setMail("paul.test@gmail.com");
		newCandidatPaul.setEmployer("Alstom");
		newCandidatPaul.setJob("Ingénieur système");
		newCandidatPaul.setHighestDiploma("MASTER");
		newCandidatPaul.setHighestDiplomaYear("2019");
		newCandidatPaul.setRole(PersonRole.APPLICANT);
		newCandidatPaul.setPersonInChargeMail("tempUserAdminManager@mail.com");

		JsonNode newCandidatPaulJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newCandidatPaul).getAsJsonObject());
		((ObjectNode) newCandidatPaulJsonNode).put("monthlyWage", "2150");
		((ObjectNode) newCandidatPaulJsonNode).putNull("urlDocs");
		applicantBusinessController.createApplicant(newCandidatPaulJsonNode, UserRole.MANAGER_ADMIN);

		Person newCandidatCyprien = new Person();
		newCandidatCyprien.setSurname("Cyprien");
		newCandidatCyprien.setName("Test");
		newCandidatCyprien.setMail("cyprien.test@gmail.com");
		newCandidatCyprien.setEmployer("ALTEN");
		newCandidatCyprien.setJob("Chef de projet IT");
		newCandidatCyprien.setHighestDiploma("ENSISA");
		newCandidatCyprien.setHighestDiplomaYear("2019");
		newCandidatCyprien.setRole(PersonRole.CONSULTANT);
		newCandidatCyprien.setPersonInChargeMail("tempUserAdminManager@mail.com");

		JsonNode newCandidatCyprienJsonNode = JsonUtils
				.toJsonNode(gson.toJsonTree(newCandidatCyprien).getAsJsonObject());
		((ObjectNode) newCandidatCyprienJsonNode).put("monthlyWage", "4525");
		((ObjectNode) newCandidatCyprienJsonNode).putNull("urlDocs");
		consultantBusinessController.createConsultant(newCandidatCyprienJsonNode, UserRole.MANAGER_ADMIN);

		Person newCandidatJeanClaude = new Person();
		newCandidatJeanClaude.setSurname("Jean-Claude");
		newCandidatJeanClaude.setName("Test");
		newCandidatJeanClaude.setMail("jc.test@gmail.com");
		newCandidatJeanClaude.setEmployer("ALTEN");
		newCandidatJeanClaude.setJob("DevOps");
		newCandidatJeanClaude.setHighestDiploma("EPITECH");
		newCandidatJeanClaude.setHighestDiplomaYear("2019");
		newCandidatJeanClaude.setRole(PersonRole.CONSULTANT);
		newCandidatJeanClaude.setPersonInChargeMail("tempUserAdminManager@mail.com");

		JsonNode newCandidatJeanClaudeJsonNode = JsonUtils
				.toJsonNode(gson.toJsonTree(newCandidatJeanClaude).getAsJsonObject());
		((ObjectNode) newCandidatJeanClaudeJsonNode).put("monthlyWage", "3215");
		((ObjectNode) newCandidatJeanClaudeJsonNode).putNull("urlDocs");
		consultantBusinessController.createConsultant(newCandidatJeanClaudeJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate database with forums
	 * 
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createForums() throws IOException {
		Forum newForum = new Forum();
		newForum.setName("EPITECH");
		newForum.setDate("2019");
		newForum.setPlace("Nancy");
		JsonNode newForumJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newForum).getAsJsonObject());
		forumBusinessController.createForum(newForumJsonNode, UserRole.MANAGER_ADMIN);

		Forum newForum1 = new Forum();
		newForum1.setName("AlsaTech");
		newForum1.setDate("2019");
		newForum1.setPlace("Strasbourg");
		JsonNode newForum1JsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newForum1).getAsJsonObject());
		forumBusinessController.createForum(newForum1JsonNode, UserRole.MANAGER_ADMIN);

		Forum newForum2 = new Forum();
		newForum2.setName("ENSISA");
		newForum2.setDate("2019");
		newForum2.setPlace("Schiltigheim");
		JsonNode newForum2JsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newForum2).getAsJsonObject());
		forumBusinessController.createForum(newForum2JsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate database with sectors
	 * 
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createSectors() throws IOException {
		Sector newSector = new Sector();
		newSector.setName("Aéronautique");
		JsonNode newSectorJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newSector).getAsJsonObject());
		sectorBusinessController.createSector(newSectorJsonNode, UserRole.MANAGER_ADMIN);

		Sector newSector1 = new Sector();
		newSector1.setName("Banque");
		JsonNode newSector1JsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newSector1).getAsJsonObject());
		sectorBusinessController.createSector(newSector1JsonNode, UserRole.MANAGER_ADMIN);

		Sector newSector2 = new Sector();
		newSector2.setName("Grande distribution");
		JsonNode newSector2JsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newSector2).getAsJsonObject());
		sectorBusinessController.createSector(newSector2JsonNode, UserRole.MANAGER_ADMIN);

		Sector newSector3 = new Sector();
		newSector3.setName("Ferroviaire");
		JsonNode newSector3JsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newSector3).getAsJsonObject());
		sectorBusinessController.createSector(newSector3JsonNode, UserRole.MANAGER_ADMIN);

		Sector newSector4 = new Sector();
		newSector4.setName("Automobile");
		JsonNode newSector4JsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newSector4).getAsJsonObject());
		sectorBusinessController.createSector(newSector4JsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 * Create and populate Database with TechSkills
	 * 
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createTechSkills() throws IOException {
		TechSkill newCpp = new TechSkill();
		newCpp.setName("C++");
		JsonNode newCppJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newCpp).getAsJsonObject());
		techSkillBusinessController.createTechSkill(newCppJsonNode, UserRole.MANAGER_ADMIN);

		TechSkill newAngular = new TechSkill();
		newAngular.setName("Angular");
		JsonNode newAngularJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newAngular).getAsJsonObject());
		techSkillBusinessController.createTechSkill(newAngularJsonNode, UserRole.MANAGER_ADMIN);

		TechSkill newCSharp = new TechSkill();
		newCSharp.setName("C#");
		JsonNode newCSharpJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newCSharp).getAsJsonObject());
		techSkillBusinessController.createTechSkill(newCSharpJsonNode, UserRole.MANAGER_ADMIN);

		TechSkill newJS = new TechSkill();
		newJS.setName("JavaScript");
		JsonNode newJSJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newJS).getAsJsonObject());
		techSkillBusinessController.createTechSkill(newJSJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * Create and populate Database with SoftSkills
	 * 
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createSoftSkills() throws IOException {
		SoftSkill newVCycle = new SoftSkill();
		newVCycle.setName("Cycle en V");
		JsonNode newVCycleJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newVCycle).getAsJsonObject());
		softSkillBusinessController.createSoftSkill(newVCycleJsonNode, UserRole.MANAGER_ADMIN);

		SoftSkill newProjet = new SoftSkill();
		newProjet.setName("Gestion de projet");
		JsonNode newProjetJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newProjet).getAsJsonObject());
		softSkillBusinessController.createSoftSkill(newProjetJsonNode, UserRole.MANAGER_ADMIN);

		SoftSkill newCom = new SoftSkill();
		newCom.setName("Communication");
		JsonNode newComJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newCom).getAsJsonObject());
		softSkillBusinessController.createSoftSkill(newComJsonNode, UserRole.MANAGER_ADMIN);
	}

	/**
	 *
	 * @author Andy Chabalier
	 * @throws IOException if an I/O problem occur
	 */
	private void createAgencies() throws IOException {
		Agency agencyStrasbourg = new Agency();
		agencyStrasbourg.setName("Strasbourg");
		JsonNode agencyStrasbourgJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyStrasbourg).getAsJsonObject());
		((ObjectNode) agencyStrasbourgJsonNode).put("place", "Schiltigheim");
		((ObjectNode) agencyStrasbourgJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyStrasbourgJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyMulhouse = new Agency();
		agencyMulhouse.setName("Mulhouse");
		JsonNode agencyMulhouseJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyMulhouse).getAsJsonObject());
		((ObjectNode) agencyMulhouseJsonNode).put("place", "Mulhouse");
		((ObjectNode) agencyMulhouseJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyMulhouseJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyLille = new Agency();
		agencyLille.setName("Lille");
		JsonNode agencyLilleJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyLille).getAsJsonObject());
		((ObjectNode) agencyLilleJsonNode).put("place", "Villeneuve-d'Ascq");
		((ObjectNode) agencyLilleJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyLilleJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyParis = new Agency();
		agencyParis.setName("Paris");
		JsonNode agencyParisJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyParis).getAsJsonObject());
		((ObjectNode) agencyParisJsonNode).put("place", "Boulogne-Billancourt");
		((ObjectNode) agencyParisJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyParisJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyRennes = new Agency();
		agencyRennes.setName("Rennes");
		JsonNode agencyRennesJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyRennes).getAsJsonObject());
		((ObjectNode) agencyRennesJsonNode).put("place", "Rennes");
		((ObjectNode) agencyRennesJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyRennesJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyNantes = new Agency();
		agencyNantes.setName("Nantes");
		JsonNode agencyNantesJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyNantes).getAsJsonObject());
		((ObjectNode) agencyNantesJsonNode).put("place", "Saint-Herblain");
		((ObjectNode) agencyNantesJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyNantesJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyBordeaux = new Agency();
		agencyBordeaux.setName("Bordeaux");
		JsonNode agencyBordeauxJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyBordeaux).getAsJsonObject());
		((ObjectNode) agencyBordeauxJsonNode).put("place", "Merignac");
		((ObjectNode) agencyBordeauxJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyBordeauxJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyLyon = new Agency();
		agencyLyon.setName("Lyon");
		JsonNode agencyLyonJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyLyon).getAsJsonObject());
		((ObjectNode) agencyLyonJsonNode).put("place", "Villeurbanne");
		((ObjectNode) agencyLyonJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyLyonJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyGrenoble = new Agency();
		agencyGrenoble.setName("Grenoble");
		JsonNode agencyGrenobleJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyGrenoble).getAsJsonObject());
		((ObjectNode) agencyGrenobleJsonNode).put("place", "Grenoble");
		((ObjectNode) agencyGrenobleJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyGrenobleJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyToulouse = new Agency();
		agencyToulouse.setName("Toulouse");
		JsonNode agencyToulouseJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyToulouse).getAsJsonObject());
		((ObjectNode) agencyToulouseJsonNode).put("place", "Labège");
		((ObjectNode) agencyToulouseJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyToulouseJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyAixEnProvence = new Agency();
		agencyAixEnProvence.setName("Aix-en-Provence");
		JsonNode agencyAixEnProvenceJsonNode = JsonUtils
				.toJsonNode(gson.toJsonTree(agencyAixEnProvence).getAsJsonObject());
		((ObjectNode) agencyAixEnProvenceJsonNode).put("place", "Aix-en-Provence");
		((ObjectNode) agencyAixEnProvenceJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyAixEnProvenceJsonNode, UserRole.MANAGER_ADMIN);

		Agency agencyNice = new Agency();
		agencyNice.setName("Nice");
		JsonNode agencyNiceJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(agencyNice).getAsJsonObject());
		((ObjectNode) agencyNiceJsonNode).put("place", "Valbonne");
		((ObjectNode) agencyNiceJsonNode).put("placeType", "city");
		AgencyBusinessController.createAgency(agencyNiceJsonNode, UserRole.MANAGER_ADMIN);

	}

	/**
	 * create and populate Database with geographics
	 *
	 * @author Andy Chabalier
	 */
	private void createGeographics() {
		geographicBusinessController.synchronize(UserRole.MANAGER_ADMIN);
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

		User userAdminManager = new User();
		userAdminManager.setForname("tempUserAdminManager");
		userAdminManager.setMail("tempUserAdminManager@mail.com");
		userAdminManager.setName("tempUserAdminManagerName");

		User userAdminCDR = new User();
		userAdminCDR.setForname("tempUserAdminCDR");
		userAdminCDR.setMail("tempUserAdminCDR@mail.com");
		userAdminCDR.setName("tempUserAdminCDRName");

		User userManager = new User();
		userManager.setForname("tempUserManager");
		userManager.setMail("tempUserManager@mail.com");
		userManager.setName("tempUserManagerName");

		User userCDR = new User();
		userCDR.setForname("tempUserCDR");
		userCDR.setMail("tempUserCDR@mail.com");
		userCDR.setName("tempUserCDRName");

		User userConsultant = new User();
		userConsultant.setForname("tempUserConsultant");
		userConsultant.setMail("tempUserConsultant@mail.com");
		userConsultant.setName("tempUserConsultantName");

		User userDeactivated = new User();
		userDeactivated.setForname("");
		userDeactivated.setMail("deactivated" + System.currentTimeMillis());
		userDeactivated.setName("");

		JsonNode userAdminManagerJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userAdminManager).getAsJsonObject());
		((ObjectNode) userAdminManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminManagerJsonNode).put("role", "MANAGER_ADMIN");
		((ObjectNode) userAdminManagerJsonNode).put("agency", "Strasbourg");
		JsonNode userAdminCDRJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userAdminCDR).getAsJsonObject());
		((ObjectNode) userAdminCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userAdminCDRJsonNode).put("role", "CDR_ADMIN");
		((ObjectNode) userAdminCDRJsonNode).put("agency", "Strasbourg");
		JsonNode userManagerJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userManager).getAsJsonObject());
		((ObjectNode) userManagerJsonNode).put("pswd", "pass");
		((ObjectNode) userManagerJsonNode).put("role", "MANAGER");
		((ObjectNode) userManagerJsonNode).put("agency", "Strasbourg");
		JsonNode userCDRJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userCDR).getAsJsonObject());
		((ObjectNode) userCDRJsonNode).put("pswd", "pass");
		((ObjectNode) userCDRJsonNode).put("role", "CDR");
		((ObjectNode) userCDRJsonNode).put("agency", "Strasbourg");
		JsonNode userConsultantJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userConsultant).getAsJsonObject());
		((ObjectNode) userConsultantJsonNode).put("pswd", "pass");
		((ObjectNode) userConsultantJsonNode).put("role", "CONSULTANT");
		((ObjectNode) userConsultantJsonNode).put("agency", "Strasbourg");
		JsonNode userDeactivatedJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(userDeactivated).getAsJsonObject());
		((ObjectNode) userDeactivatedJsonNode).put("pswd", "pass");
		((ObjectNode) userDeactivatedJsonNode).put("role", "DEACTIVATED");
		((ObjectNode) userDeactivatedJsonNode).put("agency", "Strasbourg");

		userBusinessController.createUser(userAdminManagerJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userAdminCDRJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userManagerJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userCDRJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userConsultantJsonNode, UserRole.MANAGER_ADMIN);
		userBusinessController.createUser(userDeactivatedJsonNode, UserRole.MANAGER_ADMIN);

		// Remplissage d'une population de consultant
		for (int i = 0; i < 10; i++) {
			User useri = new User();
			useri.setForname("tempUserConsultant" + i);
			useri.setMail("tempUserconsultant" + i + "@mail.com");
			useri.setName("tempUserAdminName");

			JsonNode useriJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(useri).getAsJsonObject());
			((ObjectNode) useriJsonNode).put("pswd", "pass");
			((ObjectNode) useriJsonNode).put("role", "CONSULTANT");
			((ObjectNode) useriJsonNode).put("agency", "Strasbourg");

			userBusinessController.createUser(useriJsonNode, UserRole.MANAGER_ADMIN);
		}
	}

	/**
	 * Create and populate database with skills sheets.
	 * 
	 * @throws IOException
	 * @author Lucas Royackkers
	 */
	private void createSkillsSheets() throws IOException {
		SkillsSheet newFirst = new SkillsSheet();
		newFirst.setName("JTE-mmm-AAA");
		newFirst.setMailPersonAttachedTo("jc.test@gmail.com");
		newFirst.setMailVersionAuthor("tempUserAdminManager@mail.com");
		newFirst.setRolePersonAttachedTo("consultant");

		List<TechSkill> newListTechSkill = new ArrayList<TechSkill>();
		List<SoftSkill> newLisftSoftSkill = new ArrayList<SoftSkill>();

		SoftSkill newSoftSkill = new SoftSkill();
		newSoftSkill.setName("Cycle en V");
		newSoftSkill.setGrade(SoftSkillGrade.THREE);

		SoftSkill newSoftSkill1 = new SoftSkill();
		newSoftSkill1.setName("Gestion de projet");
		newSoftSkill1.setGrade(SoftSkillGrade.ONEANDAHALF);

		SoftSkill newSoftSkill2 = new SoftSkill();
		newSoftSkill2.setName("Communication");
		newSoftSkill2.setGrade(SoftSkillGrade.TWO);

		TechSkill newTechSkill = new TechSkill();
		newTechSkill.setName("C++");
		newTechSkill.setGrade(TechSkillGrade.FOUR);

		TechSkill newTechSkill1 = new TechSkill();
		newTechSkill1.setName("Angular");
		newTechSkill1.setGrade(TechSkillGrade.TWO);

		TechSkill newTechSkill2 = new TechSkill();
		newTechSkill2.setName("C++");
		newTechSkill2.setGrade(TechSkillGrade.THREE);

		newLisftSoftSkill.add(newSoftSkill);
		newLisftSoftSkill.add(newSoftSkill1);
		newLisftSoftSkill.add(newSoftSkill2);

		newListTechSkill.add(newTechSkill);
		newListTechSkill.add(newTechSkill1);
		newListTechSkill.add(newTechSkill2);

		newFirst.setSoftSkillsList(newLisftSoftSkill);
		newFirst.setTechSkillsList(newListTechSkill);

		JsonNode newFirstJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newFirst).getAsJsonObject());
		((ObjectNode) newFirstJsonNode).put("role", "consultant");
		skillsSheetBusinessController.createSkillsSheet(newFirstJsonNode, UserRole.MANAGER_ADMIN);

		SkillsSheet newSecond = new SkillsSheet();
		newSecond.setName("PTE-mmm-AAA");
		newSecond.setMailPersonAttachedTo("paul.test@gmail.com");
		newSecond.setMailVersionAuthor("tempUserAdminManager@mail.com");
		newSecond.setRolePersonAttachedTo("applicant");

		List<TechSkill> newListTechSkillBis = new ArrayList<TechSkill>();
		List<SoftSkill> newLisftSoftSkillBis = new ArrayList<SoftSkill>();

		SoftSkill newSoftSkillBis = new SoftSkill();
		newSoftSkillBis.setName("Cycle en V");
		newSoftSkillBis.setGrade(SoftSkillGrade.THREE);

		SoftSkill newSoftSkill1Bis = new SoftSkill();
		newSoftSkill1Bis.setName("Gestion de projet");
		newSoftSkill1Bis.setGrade(SoftSkillGrade.ONEANDAHALF);

		SoftSkill newSoftSkill2Bis = new SoftSkill();
		newSoftSkill2Bis.setName("Communication");
		newSoftSkill2Bis.setGrade(SoftSkillGrade.TWO);

		TechSkill newTechSkillBis = new TechSkill();
		newTechSkillBis.setName("C++");
		newTechSkillBis.setGrade(TechSkillGrade.FOUR);

		TechSkill newTechSkill1Bis = new TechSkill();
		newTechSkill1Bis.setName("Angular");
		newTechSkill1Bis.setGrade(TechSkillGrade.TWO);

		TechSkill newTechSkill2Bis = new TechSkill();
		newTechSkill2Bis.setName("C++");
		newTechSkill2Bis.setGrade(TechSkillGrade.THREE);

		newLisftSoftSkillBis.add(newSoftSkillBis);
		newLisftSoftSkillBis.add(newSoftSkill1Bis);
		newLisftSoftSkillBis.add(newSoftSkill2Bis);

		newListTechSkillBis.add(newTechSkillBis);
		newListTechSkillBis.add(newTechSkill1Bis);
		newListTechSkillBis.add(newTechSkill2Bis);

		newSecond.setSoftSkillsList(newLisftSoftSkillBis);
		newSecond.setTechSkillsList(newListTechSkillBis);

		JsonNode newSecondJsonNode = JsonUtils.toJsonNode(gson.toJsonTree(newSecond).getAsJsonObject());
		((ObjectNode) newSecondJsonNode).put("role", "applicant");
		skillsSheetBusinessController.createSkillsSheet(newSecondJsonNode, UserRole.MANAGER_ADMIN);
	}
}