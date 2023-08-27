package eu.chessdata.chesspairing.algoritms.javafo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import eu.chessdata.chesspairing.algoritms.fideswissduch.Algorithm;
import eu.chessdata.chesspairing.algoritms.fideswissduch.v2.FideSwissDutchTest;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingRound;
import eu.chessdata.chesspairing.model.ChesspairingTournament;
import eu.chessdata.chesspairing.model.TestUtils;
import eu.chessdata.chesspairing.tools.Tools;
import eu.chessdata.chesspairing.tools.Trf;

public class JavafoWrappTest {
	private ChesspairingTournament loadFile(String fileProjectPath) {
		InputStream inputStream = FideSwissDutchTest.class.getResourceAsStream(fileProjectPath);
		Reader reader;
		try {
			reader = new InputStreamReader(inputStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage());
		}
		Gson gson = Tools.getGson();
		ChesspairingTournament tournament = gson.fromJson(reader, ChesspairingTournament.class);
		return tournament;
	}

	@Test
	public void test1() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test1.json");
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest1.json");
	}

	@Test
	public void test2() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test2.json");
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest2.json");
	}

	/**
	 * It fixes the get buyValue in case is not set
	 */
	@Test
	public void test3() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test3.json");
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest3.json");
	}

	/**
	 * Pare considering absent players
	 */
	@Test
	public void test4() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test4.json");

		// Prepare 2 absent players
		ChesspairingPlayer bogdanMarian = tournament.getPlayer("-KK4Psw2LOj9QZDFSY8F");
		ChesspairingPlayer catalin = tournament.getPlayer("-KKtt0OkvrEuE1s0aEqb");

		int roundNumber = tournament.getRounds().size() + 1;
		ChesspairingRound round = new ChesspairingRound();
		round.setRoundNumber(roundNumber);
		round.addAbsentPlayer(bogdanMarian);
		round.addAbsentPlayer(catalin);

		List<ChesspairingRound> rounds = tournament.getRounds();
		rounds.add(round);
		TestUtils.writeToFile(tournament, "javafoWrapTestTest4WithAbsence.json");

		String trf = Trf.getTrf(tournament);
		System.out.println(trf);
		
		Algorithm algorithm = new JavafoWrapp();
		
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		int totalRounds = nextRoundTournament.getRounds().size();
		Assert.assertTrue("It should be 2 rounds and in fact there are " + totalRounds, totalRounds == 2);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest4.json");
	}
	
	@Test
	public void test5() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test5.json");
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest5.json");
	}
	
	@Test
	public void test6() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test6.json");
		
		String trf = Trf.getTrf(tournament);
		System.out.println(trf);
		
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest6.json");
	}
	
	@Test
	public void test7() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test7.json");
		
		String trf = Trf.getTrf(tournament);
		System.out.println(trf);
		
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest7.json");
	}
	
	@Test
	public void test8() {
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test8.json");
		
		String trf = Trf.getTrf(tournament);
		System.out.println(trf);
		
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest8.json");
	}

	@Test
	public void test09UseAlsoDoubleForfeitResults(){
		ChesspairingTournament tournament = TestUtils.loadFile("/jafafoWrapp/test09UseAlsoDoubleForfeitResults.json");
		Assert.assertNotNull(tournament);
		Algorithm algorithm = new JavafoWrapp();
		ChesspairingTournament nextRoundTournament = algorithm.generateNextRound(tournament);
		TestUtils.writeToFile(nextRoundTournament, "javafoWrapTestTest09UseAlsoDoubleForfeitResults.json");
	}
}
