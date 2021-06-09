package B1;

import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import java.io.IOException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.io.IOException;
import java.util.List;
import ca.uhn.fhir.model.primitive.IdDt;

public class PatientClass {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("ffffffff ssssssssssssss");
		FhirContext ctx = FhirContext.forR4();

		String serverBaseUrl = "http://localhost:8080/fhir";

		Patient ourPatient = new Patient();

		// you can use the Fluent API to chain calls
		// see http://hapifhir.io/doc_fhirobjects.html
		ourPatient.addName().setUse(HumanName.NameUse.OFFICIAL).addPrefix("Mr").addGiven("Sam");
		ourPatient.addIdentifier().setSystem("http://ns.electronichealth.net.au/id/hi/ihi/1.0")
				.setValue("8003608166690503");

		// increase timeouts since the server might be powered down
		// see http://hapifhir.io/doc_rest_client_http_config.html
		ctx.getRestfulClientFactory().setConnectTimeout(60 * 1000);
		ctx.getRestfulClientFactory().setSocketTimeout(60 * 1000);

		// create the RESTful client to work with our FHIR server
		// see http://hapifhir.io/doc_rest_client.html
		IGenericClient client = ctx.newRestfulGenericClient(serverBaseUrl);

		System.out.println("Press Enter to send to server: " + serverBaseUrl);

		// send our resource up - result will be stored in 'outcome'
		// see http://hapifhir.io/doc_rest_client.html#Create_-_Type
		MethodOutcome outcome = client.create().resource(ourPatient).prettyPrint().encodedXml().execute();

		IdType id = (IdType) outcome.getId();
		System.out.println("Resource is available at: " + id.getValue());

		IParser xmlParser = ctx.newJsonParser().setPrettyPrint(true);
		Patient receivedPatient = (Patient) outcome.getResource();
		System.out.println("This is what we sent up: \n" + xmlParser.encodeResourceToString(ourPatient)
				+ "\n\nThis is what we received: \n" + xmlParser.encodeResourceToString(receivedPatient));

		System.out.println("Press Enter to end.");
		System.in.read();
	}

	private static void generatePatientJson(FhirContext ctx) {
		Patient patient = new Patient();

		// FIRST AND LAST NAME
		patient.addName().setFamily("Duck").addGiven("Donald");
		// SOCIAL SECURITY NUMBER
		// https://www.hl7.org/FHIR/datatypes.html#Identifier
		// https://www.hl7.org/FHIR/identifier-registry.html

		patient.addIdentifier()
				.setType(new CodeableConcept()
						.addCoding(new Coding().setCode("SB").setSystem("http://hl7.org/fhir/v2/0203")))
				.setSystem("http://hl7.org/fhir/sid/us-ssn").setValue("123456789");

		// GENDER
		patient.setGender(AdministrativeGender.FEMALE);

		// ADDRESS INFORMATION
		patient.addAddress().setUse(AddressUse.HOME).addLine("Street name, number, direction & P.O. Box etc.")
				.setCity("Name of city, town etc.").setState("Sub-unit of country (abbreviations ok)")
				.setPostalCode("Postal/ZIP code for area");

		// CONTACT https://www.hl7.org/fhir/datatypes-examples.html#ContactPoint
		patient.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("(555) 675 5745");

		patient.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("(415) 675 5745");

		patient.addTelecom().setSystem(ContactPointSystem.EMAIL).setValue("test@test.com");

		// EMERGENCY CONTACT
		// https://www.hl7.org/FHIR/patient-definitions.html#Patient.contact
		ContactComponent emergencyContact = new ContactComponent();

		emergencyContact.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("(111) 675 5745");

		// Relationship to patient
		emergencyContact.addRelationship().addCoding().setSystem("http://hl7.org/fhir/ValueSet/v2-0131").setCode("C");

		emergencyContact.setName(new HumanName().setFamily("Duck Emergency contact").addGiven("Duke"));

		patient.addContact(emergencyContact);

		// Encode to JSON
		IParser jsonParser = ctx.newJsonParser();
		jsonParser.setPrettyPrint(true);
		String encoded = jsonParser.encodeResourceToString(patient);
		System.out.println(encoded);
	}

}
