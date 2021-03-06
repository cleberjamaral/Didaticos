package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import exemplos.MiceAndHoles;

import busca.AEstrela;
import busca.Busca;
import busca.BuscaIterativo;
import busca.BuscaLargura;
import busca.BuscaProfundidade;
import busca.Nodo;
import busca.SubidaMontanha;

public class MiceAndHolesTests {
	static ArrayList<RespostaAlgoritmo> listaResultados = new ArrayList<RespostaAlgoritmo>();
	static boolean algoritmosAtivos[] = {true, true, true, true, true}; //BP, BSM, A*, BL, BPI
	static int MAX_CENARIO = 10;
	static int MAX_TEMPO = 120000;
	static int MAX_VISITADOS = -1;
	static int MAX_ABERTOS = 1000000;

	
	public static void main(String[] a) throws Exception {

		String str;
		BufferedReader teclado;
		teclado = new BufferedReader(new InputStreamReader(System.in));

		File file = new File("output.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		
		str = "0";
		while (!str.equals("S")) {
			System.out.print("Digite sua opcao de busca { 'S' para Sair }\n");
			System.out.print("\t1  -  Rodar um algoritmo por vez\n");
			System.out.print("\t2  -  Rodar cenarios com taxa de ocupacao fixa em 28%\n");
			System.out.print("\t3  -  Rodar cenarios com taxa de ocupacao fixa em 80%\n");
			System.out.print("\t4  -  Rodar cenarios com taxa de ocupacao fixa em 100%\n");
			System.out.print("\t5  -  Rodar cenarios com taxas de 28, 80 e 100%\n");
			System.out.print("\t6  -  Rodar heuristicas de 1 a 4 com taxas de 28, 80 e 100%\n");
			System.out.print("Opcao: ");
			str = teclado.readLine().toUpperCase();
			
			if (!str.equals("S")) {
				int contexto, cenario, heuristica;
				System.out.print("\tDigite a heuristica: 1 a 5\n");
				heuristica = Integer.parseInt(teclado.readLine());
				switch (Integer.parseInt(str)) {
				case 1:
					System.out.print("\tDigite o contexto: 1 a 3 (28, 80 ou 100% de densidade)\n");
					contexto = Integer.parseInt(teclado.readLine());
					System.out.print("\tDigite o cenario: 1 a 10 (4 a 1000 ratos)\n");
					cenario = Integer.parseInt(teclado.readLine());
					if (contexto == 1){
						rodarCenariosDensidade_028(cenario, heuristica);
					}else if (contexto == 2){
						rodarCenariosDensidade_080(cenario, heuristica);
					}else if (contexto == 3){
						rodarCenariosDensidade_100(cenario, heuristica);
					}else{
						System.out.println("Opcao invalida!");
					}
					break;
				case 2: 
					rodarCenariosDensidade_028(-1, heuristica);
					gravarResultados("h"+heuristica+"028");
					break;
				case 3: 
					rodarCenariosDensidade_080(-1, heuristica);
					gravarResultados("h"+heuristica+"080");
					break;
				case 4: 
					rodarCenariosDensidade_100(-1, heuristica);
					gravarResultados("h"+heuristica+"100");
					break;
				case 5: 
					System.setOut(ps);

					System.out.println("Apagando arquivos CSV da pasta raiz...");
					Runtime.getRuntime().exec(new String[] { "sh", "-c", "rm h"+heuristica+"*.csv" });

					rodarCenariosDensidade_028(-1, heuristica);
					gravarResultados("h"+heuristica+"028");

					rodarCenariosDensidade_080(-1, heuristica);
					gravarResultados("h"+heuristica+"080");

					rodarCenariosDensidade_100(-1, heuristica);
					gravarResultados("h"+heuristica+"100");

					System.setOut(System.out);
					break;
				case 6: 
					System.setOut(ps);

					for (;heuristica <= 5; heuristica++){
						System.out.println("Apagando arquivos CSV da pasta raiz...");
						Runtime.getRuntime().exec(new String[] { "sh", "-c", "rm h"+heuristica+"*.csv" });

						rodarCenariosDensidade_028(-1, heuristica);
						gravarResultados("h"+heuristica+"028");

						rodarCenariosDensidade_080(-1, heuristica);
						gravarResultados("h"+heuristica+"080");

						rodarCenariosDensidade_100(-1, heuristica);
						gravarResultados("h"+heuristica+"100");					
					}
					
					System.setOut(System.out);
					break;
				default:
					System.out.println("Opcao invalida!");
					break;
				}
			}
		}
		System.out.println("Saindo...");
	}

	public static void rodarAlgoritmosh2(int mP[], int hC[], int heuristica) throws Exception {

		int algoritmo = 0;

		while (algoritmo < 5) {
			RespostaAlgoritmo newTest = new RespostaAlgoritmo(mP.length);
			listaResultados.add(newTest);

			Nodo n = null;
			Busca algBusca = null;
			switch (algoritmo) {
			case 0:
				if (algoritmosAtivos[algoritmo]) {
					System.out.println("h"+heuristica+"BP (" + String.format("%04d", mP.length) + " ratos)");
					algBusca = new BuscaProfundidade(1000);
					algBusca.setMaxVisitados(MAX_VISITADOS);
					algBusca.setMaxAbertos(MAX_ABERTOS);
					algBusca.setMaxTempo(MAX_TEMPO);

					MiceAndHoles inicial = new MiceAndHoles(mP, hC, heuristica);
					n = algBusca.busca(inicial);
					if (n != null) {
						newTest.setarResultados((int) algBusca.getStatus()
								.getVisitados(), (int) algBusca.getStatus()
								.getTempoDecorrido(), (int) algBusca
								.getStatus().getTamAbertos(), algBusca.getStatus().getCustoTotal(), "BP");
//						try (FileWriter fw = new FileWriter("h"+heuristica+"BP"+ String.format("%04d", mP.length) +"Ratos.csv", false);
//								BufferedWriter bw = new BufferedWriter(fw);
//								PrintWriter out = new PrintWriter(bw)) {
//							out.print(n.montaCaminho());
//						} catch (IOException e) {
//						}					
					}else{
						//Se voltou nulo é porque foi terminado por proteção
						algoritmosAtivos[algoritmo] = false;
						System.out.println("\n\nBP Cancelado! Tempo="
								+ algBusca.getStatus().getTempoDecorrido() + ", nodos abertos="
								+ algBusca.getStatus().getTamAbertos() + ", nodos visitados=" 
								+ algBusca.getStatus().getVisitados());
					}

					inicial = null;
					algBusca = null;
					n = null;
				}
				break;
			case 1:
				if (algoritmosAtivos[algoritmo]) {
					System.out.println("\n\nh"+heuristica+"BSM (" + String.format("%04d", mP.length) + " ratos)");
					algBusca = new SubidaMontanha();
					algBusca.setMaxVisitados(MAX_VISITADOS);
					algBusca.setMaxAbertos(MAX_ABERTOS);
					algBusca.setMaxTempo(MAX_TEMPO);

					MiceAndHoles inicial = new MiceAndHoles(mP, hC, heuristica);
					n = algBusca.busca(inicial);
					if (n != null) {
						newTest.setarResultados((int) algBusca.getStatus()
								.getVisitados(), (int) algBusca.getStatus()
								.getTempoDecorrido(), (int) algBusca
								.getStatus().getTamAbertos(), algBusca
								.getStatus().getCustoTotal(), "BSM");
//						try (FileWriter fw = new FileWriter("h"+heuristica+"BSM"+ String.format("%04d", mP.length) +"Ratos.csv", false);
//								BufferedWriter bw = new BufferedWriter(fw);
//								PrintWriter out = new PrintWriter(bw)) {
//							out.print(n.montaCaminho());
//						} catch (IOException e) {
//						}					
					}else{
						//Se voltou nulo é porque foi terminado por proteção
						algoritmosAtivos[algoritmo] = false;
						System.out.println("\n\nBSM Cancelado! Tempo="
								+ algBusca.getStatus().getTempoDecorrido() + ", nodos abertos="
								+ algBusca.getStatus().getTamAbertos() + ", nodos visitados=" 
								+ algBusca.getStatus().getVisitados());
					}

					inicial = null;
					algBusca = null;
					n = null;
				}
				break;
			case 2:
				if (algoritmosAtivos[algoritmo]) {
					System.out.println("\n\nh"+heuristica+"A* (" + String.format("%04d", mP.length) + " ratos)");
					algBusca = new AEstrela();
					algBusca.setMaxVisitados(MAX_VISITADOS);
					algBusca.setMaxAbertos(MAX_ABERTOS);
					AEstrela aE = (AEstrela)algBusca;
					aE.setMaxAbertos(MAX_ABERTOS);
					algBusca.setMaxTempo(MAX_TEMPO);
					
					MiceAndHoles inicial = new MiceAndHoles(mP, hC, heuristica);
					n = algBusca.busca(inicial);
					if (n != null) {
						newTest.setarResultados((int) algBusca.getStatus()
								.getVisitados(), (int) algBusca.getStatus()
								.getTempoDecorrido(), (int) algBusca
								.getStatus().getTamAbertos(), algBusca
								.getStatus().getCustoTotal(), "A*");
//						try (FileWriter fw = new FileWriter("h"+heuristica+"A"+ String.format("%04d", mP.length) +"Ratos.csv", false);
//								BufferedWriter bw = new BufferedWriter(fw);
//								PrintWriter out = new PrintWriter(bw)) {
//							out.print(n.montaCaminho());
//						} catch (IOException e) {
//						}					
					}else{
						//Se voltou nulo é porque foi terminado por proteção
						algoritmosAtivos[algoritmo] = false;
						System.out.println("\n\nA* Cancelado! Tempo="
								+ algBusca.getStatus().getTempoDecorrido() + ", nodos abertos="
								+ algBusca.getStatus().getTamAbertos() + ", nodos visitados=" 
								+ algBusca.getStatus().getVisitados());
					}

					inicial = null;
					algBusca = null;
					n = null;
				}
				break;
			case 3:
				if (algoritmosAtivos[algoritmo]) {
					System.out.println("\n\nh"+heuristica+"BL (" + String.format("%04d", mP.length) + " ratos)");
					algBusca = new BuscaLargura();
					algBusca.setMaxVisitados(MAX_VISITADOS);
					algBusca.setMaxAbertos(MAX_ABERTOS);
					algBusca.setMaxTempo(MAX_TEMPO);
					
					MiceAndHoles inicial = new MiceAndHoles(mP, hC, heuristica);
					n = algBusca.busca(inicial);
					if (n != null) {
						newTest.setarResultados((int) algBusca.getStatus()
								.getVisitados(), (int) algBusca.getStatus()
								.getTempoDecorrido(), (int) algBusca
								.getStatus().getTamAbertos(), algBusca
								.getStatus().getCustoTotal(), "BL");
//						try (FileWriter fw = new FileWriter("h"+heuristica+"BL"+ String.format("%04d", mP.length) +"Ratos.csv", false);
//								BufferedWriter bw = new BufferedWriter(fw);
//								PrintWriter out = new PrintWriter(bw)) {
//							out.print(n.montaCaminho());
//						} catch (IOException e) {
//						}					
					}else{
						//Se voltou nulo é porque foi terminado por proteção
						algoritmosAtivos[algoritmo] = false;
						System.out.println("\n\nBL Cancelado! Tempo="
								+ algBusca.getStatus().getTempoDecorrido() + ", nodos abertos="
								+ algBusca.getStatus().getTamAbertos() + ", nodos visitados=" 
								+ algBusca.getStatus().getVisitados());
					}

					inicial = null;
					algBusca = null;
					n = null;
				}
				break;
			case 4:
				if (algoritmosAtivos[algoritmo]) {
					System.out.println("\n\nh"+heuristica+"BPI (" + String.format("%04d", mP.length) + " ratos)");
					algBusca = new BuscaIterativo();
					algBusca.setMaxVisitados(MAX_VISITADOS);
					algBusca.setMaxAbertos(MAX_ABERTOS);
					algBusca.setMaxTempo(MAX_TEMPO);
					
					MiceAndHoles inicial = new MiceAndHoles(mP, hC, heuristica);
					n = algBusca.busca(inicial);
					if (n != null) {
						newTest.setarResultados((int) algBusca.getStatus()
								.getVisitados(), (int) algBusca.getStatus()
								.getTempoDecorrido(), (int) algBusca
								.getStatus().getTamAbertos(), algBusca
								.getStatus().getCustoTotal(), "BPI");
//						try (FileWriter fw = new FileWriter("h"+heuristica+"BPI"+ String.format("%04d", mP.length) +"Ratos.csv", false);
//								BufferedWriter bw = new BufferedWriter(fw);
//								PrintWriter out = new PrintWriter(bw)) {
//							out.print(n.montaCaminho());
//						} catch (IOException e) {
//						}					
					}else{
						//Se voltou nulo é porque foi terminado por proteção
						algoritmosAtivos[algoritmo] = false;
						System.out.println("\n\nBPI Cancelado! Tempo="
								+ algBusca.getStatus().getTempoDecorrido() + ", nodos abertos="
								+ algBusca.getStatus().getTamAbertos() + ", nodos visitados=" 
								+ algBusca.getStatus().getVisitados());
					}

					inicial = null;
					algBusca = null;
					n = null;
				}
				break;
			default:
				System.out.println("Opcao invalida!");
				break;
			}
			algoritmo++;
		}
	}

	public static class RespostaAlgoritmo {
		int qtRatos;
		int qtNodosVisitados;
		int tempoDecorrido;
		int qtNovosAbertos;
		int custoTotal;
		String algoritmo;

		public int getQtRatos() {
			return qtRatos;
		}

		public int getQtNodosVisitados() {
			return qtNodosVisitados;
		}

		public int getTempoDecorrido() {
			return tempoDecorrido;
		}

		public int getQtNovosAbertos() {
			return qtNovosAbertos;
		}

		public int getCustoTotal() {
			return custoTotal;
		}

		public String getAlgoritmo() {
			return algoritmo;
		}

		public RespostaAlgoritmo(int qtRatos) {
			super();
			this.qtRatos = qtRatos;
			this.qtNodosVisitados = 0;
			this.tempoDecorrido = 0;
			this.qtNovosAbertos = 0;
			this.custoTotal = 0;
			this.algoritmo = "";
		}


		public void setarResultados(int qtNodosVisitados, int tempoDecorrido,
				int qtNovosAbertos, int custoTotal, String algoritmo) {
			this.qtNodosVisitados = qtNodosVisitados;
			this.tempoDecorrido = tempoDecorrido;
			this.qtNovosAbertos = qtNovosAbertos;
			this.custoTotal = custoTotal;
			this.algoritmo = algoritmo;
		}
	}

	public static void gravarResultados(String testeRealizado) {
		int cenarioCorrente;
		int proximoCenario;
	
		// Grava resultado de Nodos Visitados
		cenarioCorrente = 4;
		proximoCenario = 4;

		try (FileWriter fw = new FileWriter(testeRealizado+"Visitados.csv", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print("QtRatos,BP,BSM,A*,BL,BPI,\n");
			// Itera pela quantidade de cenarios que e a quantidade de
			// resultados dividido por 5 algoritmos que rodaram em cada 
			// cenario (BP, BSM, A*, BL, BPI)
			for (int i = 0; i < listaResultados.size() / 5; i++) {

				out.print(cenarioCorrente + ", ");

				for (RespostaAlgoritmo element : listaResultados) {
					// Imprime o campo qtRatos do cenario corrente
					if (element.getQtRatos() == cenarioCorrente) {
						//Se o algoritmo esta ativo o nome do algoritmo nao e nulo
						//Sempre imprime a primeira linha do csv para evitar erro no latex
						if (element.algoritmo != "" || element.getQtRatos() == 4)
							if (element.getQtNodosVisitados() < 100000) 
								out.print(element.getQtNodosVisitados() + ", ");
							else
								out.print(100000 + ", ");
						else
							out.print(", ");
					} else {
						if ((proximoCenario == cenarioCorrente)
								&& (element.getQtRatos() > proximoCenario))
							proximoCenario = element.getQtRatos();
					}
				}
				out.print("\n");

				cenarioCorrente = proximoCenario;
			}
		} catch (IOException e) {
		}

		// Grava resultado de Nodos Abertos 
		cenarioCorrente = 4;
		proximoCenario = 4;

		try (FileWriter fw = new FileWriter(testeRealizado+"Abertos.csv", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print("QtRatos,BP,BSM,A*,BL,BPI,\n");
			// Itera pela quantidade de cenarios que e a quantidade de
			// resultados dividido por 5 algoritmos que rodaram em cada 
			// cenario (BP, BSM, A*, BL, BPI)
			for (int i = 0; i < listaResultados.size() / 5; i++) {

				out.print(cenarioCorrente + ", ");

				for (RespostaAlgoritmo element : listaResultados) {
					// Imprime o campo qtRatos do cenario corrente
					if (element.getQtRatos() == cenarioCorrente) {
						//Se o algoritmo esta ativo o nome do algoritmo nao e nulo
						//Sempre imprime a primeira linha do csv para evitar erro no latex
						if (element.algoritmo != "" || element.getQtRatos() == 4)
							if (element.getQtNovosAbertos() < 100000) 
								out.print(element.getQtNovosAbertos() + ", ");
							else
								out.print(100000 + ", ");
						else
							out.print(", ");
					} else {
						if ((proximoCenario == cenarioCorrente)
								&& (element.getQtRatos() > proximoCenario))
							proximoCenario = element.getQtRatos();
					}
				}
				out.print("\n");

				cenarioCorrente = proximoCenario;
			}
		} catch (IOException e) {
		}

		// Grava resultado de Tempo decorrido 
		cenarioCorrente = 4;
		proximoCenario = 4;

		try (FileWriter fw = new FileWriter(testeRealizado+"Tempo.csv", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print("QtRatos,BP,BSM,A*,BL,BPI,\n");
			// Itera pela quantidade de cenarios que e a quantidade de
			// resultados dividido por 5 algoritmos que rodaram em cada 
			// cenario (BP, BSM, A*, BL, BPI)
			for (int i = 0; i < listaResultados.size() / 5; i++) {

				out.print(cenarioCorrente + ", ");

				for (RespostaAlgoritmo element : listaResultados) {
					// Imprime o campo qtRatos do cenario corrente
					if (element.getQtRatos() == cenarioCorrente) {
						//Se o algoritmo esta ativo o nome do algoritmo nao e nulo
						//Sempre imprime a primeira linha do csv para evitar erro no latex
						if (element.algoritmo != "" || element.getQtRatos() == 4)
							out.print(element.getTempoDecorrido() + ", ");
						else
							out.print(", ");
					} else {
						if ((proximoCenario == cenarioCorrente)
								&& (element.getQtRatos() > proximoCenario))
							proximoCenario = element.getQtRatos();
					}
				}
				out.print("\n");

				cenarioCorrente = proximoCenario;
			}
		} catch (IOException e) {
		}

		// Grava resultado de Custo 
		cenarioCorrente = 4;
		proximoCenario = 4;

		try (FileWriter fw = new FileWriter(testeRealizado+"Custo.csv", false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.print("QtRatos,BP,BSM,A*,BL,BPI,\n");
			// Itera pela quantidade de cenarios que e a quantidade de
			// resultados dividido por 5 algoritmos que rodaram em cada 
			// cenario (BP, BSM, A*, BL, BPI)
			for (int i = 0; i < listaResultados.size() / 5; i++) {

				out.print(cenarioCorrente + ", ");

				for (RespostaAlgoritmo element : listaResultados) {
					// Imprime o campo qtRatos do cenario corrente
					if (element.getQtRatos() == cenarioCorrente) {
						//Se o algoritmo esta ativo o nome do algoritmo nao e nulo
						//Sempre imprime a primeira linha do csv para evitar erro no latex
						if (element.algoritmo != "" || element.getQtRatos() == 4)
							if (element.getCustoTotal() < 100000) 
								out.print(element.getCustoTotal() + ", ");
							else
								out.print(100000 + ", ");
						else
							out.print(", ");
					} else {
						if ((proximoCenario == cenarioCorrente)
								&& (element.getQtRatos() > proximoCenario))
							proximoCenario = element.getQtRatos();
					}
				}
				out.print("\n");

				cenarioCorrente = proximoCenario;
			}
		} catch (IOException e) {
		}
		listaResultados.clear();
	}
	
	private static void rodarCenariosDensidade_028(int selecao, int heuristica) throws Exception {
		int cenario, cenarioFinal;

		if (selecao == -1) {
			cenario = 0;
			cenarioFinal = MAX_CENARIO;
		} else {
			cenario = selecao-1;
			cenarioFinal = selecao;
		}

		//Garante que todos os algoritmos iniciam ativos
		for (int i = 0; i < 5; i++) algoritmosAtivos[i] = true;

		
		while (++cenario <= cenarioFinal) {
			if (cenario == 1) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 4 ratos, densidade: 0.285714
				int micePosition[] = {1,8,1,7};
				int holeCapacity[] = {0,7,1,6,0,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 2) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 5 ratos, densidade: 0.277778
				int micePosition[] = {2,3,8,9,2};
				int holeCapacity[] = {0,0,2,3,0,4,3,0,4,2};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 3) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 6 ratos, densidade: 0.285714
				int micePosition[] = {6,8,1,9,7,3};
				int holeCapacity[] = {1,4,1,0,2,0,3,7,1,1,1,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 4) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 8 ratos, densidade: 0.285714
				int micePosition[] = {13,2,6,1,13,7,3,10};
				int holeCapacity[] = {7,2,0,0,4,5,0,1,2,1,0,0,0,0,4,2};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 5) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 10 ratos, densidade: 0.285714
				int micePosition[] = {20,18,4,2,5,19,9,18,19,9};
				int holeCapacity[] = {1,0,2,5,2,2,1,1,0,1,0,1,0,0,0,1,4,2,5,7};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 6) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 20 ratos, densidade: 0.285714
				int micePosition[] = {22,10,8,33,18,15,35,37,34,34,15,1,15,19,15,27,23,18,4,27};
				int holeCapacity[] = {0,7,1,0,0,2,0,0,5,0,4,2,2,1,4,0,3,0,0,3,4,1,1,0,1,0,0,5,2,0,0,3,7,7,0,0,0,4,1,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 7) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 100 ratos, densidade: 0.285714
				int micePosition[] = {31,89,73,198,36,100,153,164,194,38,139,196,168,161,45,160,69,76,61,165,71,8,59,164,161,142,66,93,56,93,127,159,129,192,53,49,139,189,33,32,188,93,95,153,34,135,97,150,5,36,9,100,73,98,118,21,135,62,121,132,128,14,73,25,133,46,41,81,15,166,7,6,111,24,24,145,140,187,98,31,104,184,84,195,73,12,1,176,187,122,192,87,69,160,18,39,53,99,78,84};
				int holeCapacity[] = {1,0,2,0,0,2,2,1,2,0,1,2,0,0,4,0,1,1,1,7,2,2,0,7,7,2,0,1,0,7,5,0,1,0,4,4,1,1,3,0,4,1,2,0,1,1,2,1,0,0,7,0,7,1,0,0,1,0,0,0,1,1,2,4,1,2,5,0,1,1,2,4,0,4,0,0,1,1,1,1,7,1,1,2,0,1,1,3,3,0,0,8,0,7,0,2,0,0,2,2,2,0,0,1,0,4,1,5,7,0,0,1,2,1,1,5,4,0,1,0,1,0,1,1,0,0,0,0,4,1,1,0,1,0,3,7,1,0,1,1,0,1,4,7,9,1,1,2,2,0,8,0,3,1,0,0,0,0,1,4,4,0,0,1,0,0,1,0,0,0,0,1,0,1,7,4,7,0,7,1,0,2,3,2,0,0,0,7,1,4,0,4,2,1,2,0,0,7,1,4};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 8) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 300 ratos, densidade: 0.285714
				int micePosition[] = {595,167,471,430,377,575,413,255,548,580,4,161,376,253,488,314,375,263,65,330,389,457,381,463,328,88,162,550,324,493,522,65,143,247,309,179,499,600,320,255,435,68,568,529,454,172,86,542,140,242,507,171,86,403,126,236,126,366,189,313,148,550,310,375,47,413,171,203,344,327,353,144,98,559,386,190,212,59,127,418,322,64,287,359,376,583,38,105,187,294,105,197,74,21,269,247,42,323,259,185,597,318,529,564,56,277,339,319,566,586,85,41,321,381,4,383,176,309,55,276,527,454,27,132,53,108,438,106,120,312,310,276,527,245,524,258,137,284,386,480,120,263,272,405,430,36,345,126,232,505,182,99,589,519,396,345,445,570,103,124,7,15,147,486,232,467,29,588,314,455,557,305,522,71,90,51,36,568,180,579,294,471,371,530,154,189,338,60,238,421,506,142,172,523,270,376,69,19,438,76,561,304,247,106,265,177,123,254,352,464,457,535,169,52,586,414,282,16,266,162,360,475,440,277,361,527,249,418,238,142,383,278,247,384,587,508,120,364,596,533,268,554,495,327,120,404,420,212,304,205,21,179,229,395,372,256,228,156,237,112,447,459,123,120,266,394,317,597,286,278,22,542,76,7,153,534,554,445,80,28,279,212,296,222,536,569,209,170,118,570,61,516,424,419,397,253,457,464,18,209};
				int holeCapacity[] = {0,0,0,1,0,3,1,2,0,1,2,8,1,0,1,3,0,7,0,0,1,0,0,1,0,1,2,3,1,2,9,1,1,0,0,0,2,4,1,1,1,4,1,2,7,1,3,1,2,4,2,0,4,0,7,0,0,0,4,2,2,6,2,0,1,0,5,0,1,0,7,3,0,2,1,7,1,1,0,1,0,0,0,7,0,4,8,0,2,7,0,0,2,0,0,1,1,2,0,0,2,1,4,2,1,5,2,4,5,1,1,2,0,0,0,1,0,3,1,0,4,0,8,1,0,0,0,0,7,0,0,2,0,1,0,7,2,1,0,1,1,0,1,0,2,0,1,0,0,0,0,3,1,1,1,1,1,0,1,2,1,1,1,2,1,4,0,0,8,1,1,1,4,1,4,7,3,1,0,0,4,7,4,0,1,0,1,7,0,0,4,7,7,1,1,1,3,0,2,1,0,1,4,1,0,8,0,1,0,0,1,0,3,2,0,0,1,0,0,1,1,6,1,0,2,4,4,0,5,0,0,7,1,7,3,0,1,0,0,1,3,2,1,2,8,4,2,6,1,0,1,0,2,0,1,2,3,0,1,2,7,0,2,0,1,0,0,1,7,0,0,0,2,8,4,0,6,0,3,0,1,1,0,3,2,7,0,5,4,0,2,1,0,1,4,0,1,1,5,1,8,0,0,0,3,2,1,3,4,7,1,0,0,0,0,0,2,1,2,1,1,0,9,0,1,1,4,3,0,2,3,0,0,0,2,4,1,2,1,0,1,2,0,0,0,1,2,7,0,0,1,1,1,0,1,0,3,0,0,0,1,1,0,5,0,0,5,0,1,0,2,7,4,2,4,1,0,0,3,5,1,1,1,0,8,0,0,4,5,1,3,0,2,8,4,1,0,4,1,7,0,1,0,8,0,0,0,0,1,0,0,0,2,2,4,2,2,2,1,2,1,0,1,1,0,5,0,0,0,2,1,0,1,0,0,8,1,1,0,7,2,0,0,2,0,7,1,1,5,8,2,2,0,0,4,1,0,0,2,8,3,7,4,1,1,1,1,1,0,0,8,4,1,0,2,0,1,0,0,4,0,1,0,3,7,0,2,4,0,5,4,0,1,1,0,0,8,1,0,0,1,2,0,0,1,0,0,4,2,0,0,1,0,2,4,0,2,4,2,3,1,1,0,1,2,0,0,1,0,0,0,0,1,0,0,4,2,0,1,1,1,0,1,0,1,1,4,4,5,2,0,8,0,1,1,0,0,1,1,7,0,0,1,7,3,1,1,2,0,0,1,5,0,0,0,1,7,2,1,2,7,1,0,2,7,2,0,1,1,0,0,2,2,1,0,2,2,1,2,1};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 9) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 500 ratos, densidade: 0.285714
				int micePosition[] = {999,329,448,547,110,437,374,14,618,384,86,448,753,717,518,915,405,951,9,284,73,406,143,61,941,934,74,983,108,635,992,906,435,326,3,832,346,890,888,580,665,804,679,632,717,306,347,514,989,829,259,975,631,306,580,781,663,353,443,678,917,516,92,164,769,232,238,508,868,978,509,563,64,655,119,993,377,628,144,906,715,393,23,738,105,394,648,526,583,751,238,911,774,216,152,744,288,434,632,73,82,287,932,986,326,853,482,639,576,573,355,881,328,681,399,121,75,105,189,649,119,524,134,266,413,38,336,782,760,758,197,243,808,958,347,367,812,362,785,110,253,45,998,581,198,523,637,779,501,295,875,977,913,468,617,875,471,774,509,216,795,559,719,145,741,494,758,315,780,702,785,844,246,290,241,144,363,715,531,566,521,664,962,682,430,996,436,465,549,766,242,441,295,937,836,136,8,810,617,827,499,553,979,821,929,651,818,444,920,307,452,338,686,729,971,537,225,682,477,716,539,967,285,528,63,779,547,489,371,868,470,19,778,918,792,142,338,134,497,981,836,10,663,672,573,717,459,448,595,76,152,429,560,465,60,728,672,173,808,733,222,847,840,383,832,750,285,814,647,385,668,378,569,94,639,552,194,550,683,361,513,706,773,674,388,344,499,869,984,483,926,568,195,935,14,109,607,895,587,721,811,784,951,195,816,741,698,247,962,91,608,711,640,326,869,210,37,906,367,408,537,346,794,194,856,83,333,304,101,356,708,450,271,216,175,726,918,609,867,350,664,944,264,262,348,213,380,875,720,750,462,486,490,592,49,233,76,369,273,577,19,525,436,423,657,707,725,154,231,550,127,912,522,679,897,981,755,892,605,719,815,462,491,65,333,200,100,522,72,592,404,106,177,338,970,530,146,342,331,734,718,154,214,893,55,47,31,520,50,410,251,642,182,565,700,553,676,989,457,936,429,661,603,663,874,552,201,135,45,405,501,382,713,717,547,222,229,909,25,738,126,757,925,397,344,257,146,143,586,515,796,645,247,791,268,875,893,440,56,632,219,673,870,532,519,286,788,145,814,841,404,649,887,9,176,54,516,851,841,257,105,640,156,143,288,936,136,902,385,331,976,242,925,465,346,927,7,208,532,622};
				int holeCapacity[] = {1,4,0,0,0,1,2,0,0,7,7,0,0,4,0,3,0,1,7,0,4,0,2,5,0,1,2,4,1,8,4,7,5,0,1,2,7,1,0,1,0,2,8,9,0,0,4,0,7,2,7,8,7,0,0,0,0,1,0,1,7,0,0,1,1,1,0,1,0,0,0,0,0,0,1,1,4,0,0,0,8,1,1,4,2,0,0,7,2,1,1,2,1,0,1,7,7,8,4,0,0,1,0,7,0,0,0,0,0,0,0,0,2,4,0,2,0,5,4,0,2,2,1,0,2,2,0,2,2,0,2,2,1,0,0,0,5,0,0,8,7,0,1,2,1,4,0,4,1,7,4,0,1,3,0,0,0,0,4,0,0,1,7,3,0,4,0,0,0,0,0,0,1,1,0,1,0,1,0,1,0,2,0,0,0,6,0,1,7,1,2,1,0,4,2,7,4,0,7,4,0,2,1,1,4,1,0,2,1,4,1,0,0,0,1,1,0,1,0,1,0,0,1,1,0,0,0,7,0,7,1,0,4,0,0,2,0,1,1,8,2,1,1,1,2,4,2,0,0,0,1,0,1,0,0,7,0,2,0,5,1,0,1,0,1,0,0,0,7,0,0,1,1,0,2,2,8,1,0,0,0,8,7,0,0,1,1,1,0,4,1,0,4,1,0,0,5,2,0,0,0,8,4,0,0,0,2,0,0,1,1,7,2,0,1,4,4,0,0,1,3,1,2,1,0,2,0,7,1,1,8,5,7,0,1,3,0,0,1,0,2,0,2,1,0,0,0,0,9,0,0,0,2,1,2,7,4,3,4,0,0,1,0,4,0,0,0,2,0,2,0,2,0,0,2,2,0,0,0,2,0,0,0,1,4,1,0,3,1,1,0,2,0,0,1,0,0,1,0,5,0,2,0,0,7,4,0,5,2,2,0,0,3,1,0,0,5,2,0,0,1,0,0,3,0,2,1,1,0,8,7,7,4,2,1,0,0,7,7,3,7,0,2,0,7,0,2,0,3,5,7,1,4,0,2,9,7,7,0,0,3,0,1,1,2,4,2,1,1,4,0,1,0,0,1,2,0,3,2,4,5,4,3,4,0,5,1,1,0,4,1,3,4,3,5,0,7,0,0,0,2,0,7,0,0,1,0,0,1,0,0,0,2,7,0,2,7,0,0,1,1,8,5,0,4,0,0,3,0,0,2,0,4,7,3,0,1,0,2,1,0,0,0,0,1,0,0,4,2,1,1,0,2,0,0,3,4,2,0,8,0,2,4,2,1,1,0,1,2,0,1,0,1,0,3,0,0,1,0,7,7,1,2,1,5,4,7,0,5,7,2,0,4,0,0,1,7,2,0,0,2,0,2,1,2,0,0,0,0,1,2,0,0,1,2,1,1,1,1,7,1,3,3,4,0,0,0,0,4,0,4,0,0,1,0,3,0,0,1,0,2,1,4,0,4,7,7,1,8,1,1,1,4,0,1,1,4,2,7,0,0,5,2,0,2,1,4,3,7,0,4,4,0,0,2,4,2,0,0,0,7,1,7,1,1,7,0,0,0,6,0,0,0,0,0,1,2,2,0,1,2,1,0,0,1,4,0,0,0,4,1,0,1,1,0,0,1,1,0,0,0,7,5,2,1,0,0,2,1,0,8,0,1,0,4,4,0,1,0,4,4,0,4,4,0,0,8,0,2,7,0,0,0,0,1,0,1,8,0,4,2,0,1,0,1,0,1,0,0,1,1,0,2,1,2,7,4,2,1,4,2,2,5,0,0,0,0,0,0,0,7,0,4,1,4,0,8,0,1,0,1,1,4,1,0,0,1,1,7,0,0,0,0,0,7,0,0,4,1,0,4,0,0,1,3,0,1,2,4,0,0,0,4,1,3,1,4,4,0,2,0,0,0,0,4,7,0,0,1,0,0,4,0,1,2,2,0,2,0,0,0,0,3,4,3,1,0,0,1,4,0,0,0,4,0,4,2,0,0,5,1,4,2,2,0,2,8,1,7,0,3,0,0,0,1,0,1,4,0,0,1,1,0,0,1,7,0,7,1,2,1,0,0,1,7,0,1,3,4,0,0,0,1,1,5,1,1,4,0,4,4,0,2,1,4,0,0,1,2,0,1,0,0,1,4,0,7,0,2,1,0,1,0,1,4,1,0,0,0,4,0,4,0,7,0,1,0,7,2,1,0,4,1,5,4,0,4,1,0,1,1,7,0,0,1,0,0,0,0,4,0,1,4,0,0,7,0,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 10) {
				System.out.println("\nh"+heuristica+"028 Cenario "+cenario);
				//Gerado cenario 1000 ratos, densidade: 0.285714
				int micePosition[] = {357,1506,1741,654,342,1927,979,753,1738,1544,409,1858,1728,1676,1220,1174,173,480,1683,451,1771,987,224,1746,1112,786,1442,53,472,891,162,714,749,1177,1711,867,718,507,799,956,604,637,1718,1352,198,1337,444,735,1293,1035,578,543,1528,827,1079,1667,547,445,1534,673,1745,1022,945,156,871,34,247,1786,291,844,473,1998,429,369,812,1565,1871,732,65,1385,1634,499,770,33,499,340,1790,1585,1256,1059,1865,1472,1145,419,1550,1096,461,940,1611,1857,1449,538,139,485,1159,1133,1928,989,410,506,111,1078,1058,1102,1608,1363,1302,470,473,1604,1218,666,1409,853,1311,970,692,116,1991,897,1647,928,942,260,591,1936,931,56,3,1565,1097,63,1286,1810,1870,1187,285,1273,1767,262,1591,350,235,528,949,478,1129,1356,1809,99,1262,1925,40,1752,937,1085,1892,1346,504,403,1417,1442,412,246,1528,403,1456,805,1088,422,1507,1867,5,1813,1057,1091,696,1366,1753,181,244,95,1422,467,1219,591,1476,1895,1254,1689,358,1036,856,320,406,871,1532,1334,1492,1780,527,1694,1166,240,547,1858,994,1871,665,1946,1077,1394,1422,509,1947,1258,1959,1552,1130,1373,1031,771,1759,1826,608,498,781,1131,168,1774,707,956,1285,1521,322,361,387,1212,1390,116,1302,1808,27,633,443,150,1559,708,88,1364,760,475,353,1126,1011,1989,1006,1557,1880,1724,1577,1309,393,1655,1193,1306,1793,16,112,1434,1707,1863,881,1103,1807,1499,229,56,948,588,596,1565,1149,1058,1747,1801,1555,264,1451,1900,913,1379,878,1647,1110,1980,445,1251,1344,1301,522,1839,1799,1970,1184,939,1519,364,1815,1531,896,1485,280,1281,1552,473,1400,364,1638,753,831,304,413,120,1669,323,662,1888,378,1144,1371,1974,1540,908,1803,172,455,329,694,1260,520,1151,940,1321,1273,901,1160,285,920,1016,952,1913,1081,1144,1397,427,475,885,318,1072,739,244,1036,201,805,692,1242,792,676,601,1389,181,1363,26,1836,102,531,271,1776,1111,405,368,702,875,1197,140,1712,1749,289,1128,928,1653,200,1443,1972,1530,1405,1477,1728,1019,1342,236,1168,1416,1124,1086,1716,175,1294,1147,229,1840,1680,1296,86,1185,1360,1398,812,479,628,949,775,1426,1934,1888,1379,1683,1565,1263,995,1377,1531,478,581,568,1676,460,1034,1926,1959,1580,879,13,472,1209,369,1842,379,1506,982,637,156,1247,445,887,383,995,1213,934,1387,419,1945,332,1059,1473,368,847,1797,1721,1659,1827,183,1045,314,1422,1818,388,1872,1495,389,183,1586,1840,1973,936,326,820,514,1192,188,1711,857,1029,1232,9,67,1011,80,1520,714,737,831,387,190,1392,1759,513,1810,1379,1282,1313,1655,1459,1022,762,1176,205,478,1856,360,1779,624,580,879,958,1993,1445,401,30,909,1170,878,1830,1734,543,1459,8,1261,855,1511,1814,1403,731,1338,842,779,847,763,982,42,477,69,504,288,1545,254,1843,980,1286,204,380,1364,1177,374,1416,1839,1908,129,776,860,249,1339,246,1351,443,640,1544,1554,1855,1327,176,1697,920,441,270,1812,261,131,1171,188,575,1477,229,1218,1025,676,535,1494,35,1482,693,1279,1950,1851,841,643,718,1333,1340,99,994,476,1170,1693,1288,1986,710,214,1220,1274,392,1820,671,1546,1848,539,533,68,1097,1174,951,1802,1463,1366,574,1218,1695,476,943,797,1242,584,693,862,475,736,1914,53,1702,1519,234,1820,828,696,1832,714,1137,1894,396,1031,731,1332,458,1126,1878,1944,143,869,1720,1797,1072,1447,405,1844,826,1802,1133,1411,92,781,738,698,1274,968,365,1701,1004,1472,488,229,506,1014,1968,1815,616,100,1835,327,1082,1036,1426,1246,603,149,1762,174,259,662,848,1743,785,108,725,488,697,144,1934,764,196,528,776,390,1093,1496,445,927,76,701,421,1305,701,1829,1293,800,1295,125,582,1112,1356,412,1493,957,932,373,527,1808,118,1639,1921,1811,848,1450,1095,1316,1632,1024,206,1570,953,1159,512,63,701,1802,1564,1132,310,577,641,1036,1909,1543,493,298,545,1282,1508,157,156,1595,601,1298,727,1211,1072,1668,1581,737,577,236,881,1174,251,19,943,687,1535,1234,1605,797,1737,563,1053,551,71,1109,1880,582,1170,315,1899,1167,1236,1290,1806,1496,263,1642,1397,1796,1341,1101,1673,335,1131,967,1434,504,305,651,1364,1143,1833,1667,1285,1434,501,326,695,132,979,1227,320,255,1861,1651,769,471,1020,1613,197,146,1398,1324,1001,1858,1417,553,1095,1338,1675,565,1511,1478,1096,950,1391,106,909,89,750,1268,1585,1590,1484,426,714,805,1744,1760,1208,1803,1394,1383,854,859,626,364,970,1342,11,964,318,1928,1777,894,1921,1769,1072,1366,329,335,1064,1929,208,1352,1045,1607,940,1187,1994,392,31,1219,845,1201,1151,1925,283,245,1928,860,1664,357,941,432,1220,1926,732,201,17,1095,352,1238,275,1960,1540,1033,833,1343,877,1625,986,94,435,57,1333,1027,1447,832,65,1076,1938,606,1201,1365,19,546,1405,1527,780,94,1743,1958,1170,815,700,1607,713,1210,279,783,1168,895,1881,51,446,1087,1573,700,443,72,8,982,1577,353,1471,307,1233};
				int holeCapacity[] = {4,3,2,1,0,7,0,0,5,1,0,7,7,0,7,4,1,4,2,0,0,7,0,7,1,7,0,7,0,4,2,0,0,7,2,0,7,2,0,4,0,0,2,0,2,0,1,0,0,0,7,0,4,7,0,0,0,0,1,1,0,7,0,4,0,0,2,2,0,1,0,4,7,1,1,0,0,1,1,2,0,0,0,0,0,1,2,1,0,0,7,0,2,0,0,0,1,8,0,2,0,0,0,7,7,0,0,4,7,0,0,0,0,7,1,0,2,2,0,0,2,0,2,0,0,1,4,0,2,7,4,2,2,0,0,1,0,0,4,0,1,0,7,4,0,0,1,0,0,0,2,0,7,1,2,2,0,7,0,1,0,0,1,0,0,0,2,0,0,4,4,0,1,1,0,0,1,0,7,1,2,0,0,0,1,0,0,7,2,2,0,0,0,1,0,0,0,0,7,0,1,0,1,0,0,0,0,7,1,2,7,0,0,7,5,0,0,0,0,0,0,2,7,2,7,2,1,4,0,3,2,7,0,7,1,4,2,7,2,2,0,2,0,5,7,1,1,1,0,4,0,2,7,7,0,0,0,0,1,0,0,4,0,0,8,4,0,0,0,0,0,0,7,0,0,4,0,0,4,1,1,0,1,0,1,1,0,0,2,0,0,0,1,0,1,1,0,1,0,1,0,0,0,4,2,0,2,0,0,4,1,5,0,0,2,0,1,0,0,7,0,0,0,4,0,0,0,0,0,4,0,2,1,7,7,0,4,0,0,0,2,2,0,0,0,0,0,0,2,0,7,0,0,4,0,0,0,1,0,0,0,0,7,2,4,0,0,1,0,0,4,1,0,0,2,1,0,0,2,4,7,4,0,4,2,4,0,4,0,0,3,7,4,0,0,4,0,0,1,4,0,0,1,2,7,2,1,0,0,0,4,0,9,4,7,0,7,0,0,0,2,7,2,1,0,0,0,7,0,2,4,7,0,0,1,0,2,0,0,0,0,7,0,0,1,0,2,0,0,7,1,0,0,1,0,0,0,0,1,4,1,0,0,0,0,2,0,4,0,0,0,0,2,7,0,0,0,0,0,0,2,1,0,0,0,4,4,7,2,4,0,0,7,0,0,0,0,0,7,4,7,2,0,4,0,5,4,0,1,0,4,2,4,0,2,0,1,4,0,4,4,7,1,0,5,1,7,0,0,2,0,2,8,0,4,1,0,2,7,0,0,7,0,4,0,0,0,1,0,0,1,0,0,0,0,4,0,0,0,7,6,0,0,1,7,1,0,0,4,7,1,7,1,0,1,0,0,0,1,2,0,1,4,1,0,4,0,4,7,0,2,0,0,2,0,0,2,0,0,7,2,0,7,0,2,0,2,0,0,2,0,0,0,0,0,0,8,0,1,1,0,0,0,0,0,0,4,2,7,4,7,0,0,0,7,0,0,1,2,3,0,0,1,4,0,4,1,2,2,4,0,0,0,1,0,0,0,0,2,0,4,4,2,0,0,0,0,1,0,1,4,1,1,0,2,1,2,0,4,7,0,2,0,0,7,2,2,3,2,3,0,5,0,1,1,7,0,1,0,0,0,0,2,0,0,4,0,1,7,0,4,1,0,0,0,0,0,1,0,0,0,0,0,2,1,4,1,1,0,0,4,1,2,1,0,1,2,0,2,0,1,1,0,0,7,0,0,0,0,0,7,0,4,0,0,0,2,0,4,4,1,5,0,2,4,2,1,2,0,0,0,5,1,7,4,0,0,0,2,1,1,4,0,0,0,0,0,0,0,1,7,0,0,0,7,0,0,1,0,0,2,0,7,7,1,0,0,0,2,5,0,4,0,0,0,1,2,1,2,4,0,0,4,0,0,7,4,4,7,4,0,0,1,2,4,1,0,0,4,0,1,4,0,2,0,0,8,1,4,0,0,0,7,4,0,0,1,0,4,1,2,1,7,2,0,2,0,0,0,0,1,7,0,0,2,0,0,2,0,4,7,1,7,2,4,2,0,0,0,7,2,0,0,0,0,0,2,0,2,0,2,2,0,0,2,0,1,1,0,4,1,0,0,7,0,0,0,0,0,0,0,4,7,0,0,7,0,0,0,2,5,0,7,0,0,3,7,0,0,0,3,4,0,1,0,1,1,0,0,4,0,0,7,1,0,2,1,0,0,2,2,7,7,0,0,0,0,7,0,4,7,0,7,0,0,1,2,0,0,1,0,0,0,2,8,7,0,0,0,1,7,1,0,0,1,1,0,1,0,7,0,7,1,7,4,1,2,7,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,7,2,0,0,7,0,7,0,2,0,0,0,0,1,1,7,0,2,0,0,3,0,4,0,2,0,2,7,0,2,7,0,7,0,0,1,2,0,0,7,4,0,2,0,0,0,2,0,0,7,0,5,1,0,0,0,0,2,1,0,0,0,0,0,4,4,0,1,4,0,0,0,1,0,0,0,0,1,2,4,7,1,0,4,2,1,1,0,2,7,1,0,2,0,0,7,0,0,4,2,1,1,0,0,2,4,1,0,4,0,1,1,4,1,0,0,7,7,0,0,3,0,0,0,7,2,6,0,0,1,0,0,0,7,5,4,7,4,0,0,7,0,0,0,0,0,0,2,7,1,1,0,0,3,0,2,1,0,0,4,2,0,0,0,0,4,0,0,0,0,2,0,1,0,0,2,0,0,0,0,0,7,4,1,0,7,0,0,0,2,4,0,4,0,0,0,0,8,4,0,1,0,7,1,0,0,2,2,4,2,0,0,2,4,0,0,1,0,7,1,4,0,7,0,0,5,1,1,0,1,1,0,7,0,7,0,0,2,1,0,0,0,0,0,0,0,4,3,0,1,2,0,2,1,0,0,4,0,2,0,2,0,0,4,4,3,7,0,0,4,0,7,6,0,0,2,0,4,0,0,0,0,0,0,0,7,0,4,0,1,7,0,4,4,0,1,0,0,0,0,2,3,2,0,0,7,0,0,0,0,4,0,2,0,0,0,4,0,0,0,4,2,7,2,0,1,2,2,2,1,7,2,4,0,1,0,1,0,0,4,0,0,2,0,0,4,1,0,0,7,4,2,0,0,0,3,2,0,4,0,0,0,4,0,0,1,1,0,0,7,4,2,4,0,0,7,0,0,0,0,7,0,0,0,4,2,0,0,2,7,1,7,4,2,1,5,7,4,0,0,0,2,1,0,5,4,0,1,0,2,0,0,2,0,4,7,7,7,0,1,0,0,0,2,8,0,1,4,7,2,0,0,1,2,7,0,7,0,0,1,0,4,0,1,4,0,1,0,0,0,4,0,7,0,7,7,0,0,0,2,1,7,7,0,4,7,0,1,7,4,2,4,2,2,0,0,0,0,1,0,4,1,0,0,0,0,4,2,2,4,7,0,0,2,0,0,2,0,1,1,1,0,0,2,4,1,0,1,7,0,4,0,7,0,3,0,4,0,0,0,0,0,2,0,4,4,0,2,4,0,0,4,4,0,0,0,4,2,1,2,0,0,7,7,0,4,0,0,0,0,0,0,7,4,4,7,0,0,0,0,7,0,1,2,7,7,2,4,0,2,7,7,1,4,0,7,7,1,0,0,0,4,7,4,0,3,0,0,0,7,1,4,2,7,0,0,0,0,2,4,0,0,0,0,1,0,0,0,8,0,0,1,0,0,2,7,0,7,0,4,0,0,0,1,2,1,0,0,1,0,0,2,4,0,0,2,1,5,4,2,1,0,2,0,0,2,0,0,7,7,0,7,0,2,4,7,0,2,7,5,7,1,0,0,2,0,0,4,4,0,4,4,4,7,7,7,0,4,4,0,7,1,0,1,0,1,0,0,2,4,0,7,7,0,0,4,1,7,7,7,0,4,0,0,2,0,0,0,7,0,1,1,0,0,1,2,0,0,1,0,1,4,7,4,0,0,0,0,1,7,0,7,7,4,0,2,0,0,4,3,1,7,0,1,2,0,0,4,0,7,2,0,0,1,0,1,7,1,2,0,1,7,5,0,2,2,2,0,0,7,0,0,0,2,7,2,0,0,0,7,2,4,1,3,2,0,7,0,0,4,1,0,4,0,0,0,0,0,0,1,0,0,0,1,4,0,0,7,0,2,0,7,0,4,0,4,7,7,4,3,0,7,1,1,7,5,1,0,0,0,1,0,7,1,7,4,2,0,0,0,1,7,0,0,3,7,4,1,4,4,0,0,0,7,2,1,7,2,4,4,0,1,1,0,7,1,0,1,4,0,0,0,0,4,0,0,0,7,7,7,2,1,2,0,0,0,0,0,2,2,0,0,0,0,1,1,0,0,2,0,0,1,1,1,0,0,4,7,0,1,4,0,0,4,1,0,0,0,7,2,1,0,4,7,2,0,0,0,0,0,0,2,1,0,0,7,0,0,2,2,7,1,0,2,7,4,1,0,0,4,2,7,7,0,2,0,1,0,2,0,0,2,2,0,0,0,0,4,1,0,2,1,1,0};		
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			}
		}
	}

	private static void rodarCenariosDensidade_080(int selecao, int heuristica) throws Exception {
		int cenario, cenarioFinal;

		if (selecao == -1) {
			cenario = 0;
			cenarioFinal = MAX_CENARIO;
		} else {
			cenario = selecao-1;
			cenarioFinal = selecao;
		}
		
		//Garante que todos os algoritmos iniciam ativos
		for (int i = 0; i < 5; i++) algoritmosAtivos[i] = true;

		while (++cenario <= cenarioFinal) {
			if (cenario == 1) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 4 ratos, densidade: 0.800000
				int micePosition[] = {8,3,7,7};
				int holeCapacity[] = {0,0,2,0,0,3,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 2) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 5 ratos, densidade: 0.833333
				int micePosition[] = {3,3,4,10,9};
				int holeCapacity[] = {0,0,0,0,3,2,0,1,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 3) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 6 ratos, densidade: 0.857143
				int micePosition[] = {4,5,10,1,7,7};
				int holeCapacity[] = {0,5,0,0,0,0,0,2,0,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 4) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 8 ratos, densidade: 0.800000
				int micePosition[] = {13,13,1,13,9,15,10,11};
				int holeCapacity[] = {1,0,0,0,0,0,3,3,1,0,0,0,0,2,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 5) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 10 ratos, densidade: 0.833333
				int micePosition[] = {12,17,6,9,12,5,5,5,19,20};
				int holeCapacity[] = {0,5,0,2,0,0,0,0,1,0,1,0,0,1,0,0,0,2,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 6) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 20 ratos, densidade: 0.800000
				int micePosition[] = {8,33,15,13,39,35,1,27,39,32,20,37,39,29,5,13,25,9,1,21};
				int holeCapacity[] = {0,0,0,0,0,0,0,0,0,2,3,0,0,0,0,0,0,5,0,0,0,0,0,4,0,4,0,0,0,0,0,0,0,0,0,0,0,7,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 7) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 100 ratos, densidade: 0.800000
				int micePosition[] = {163,155,133,20,106,19,172,109,45,128,71,84,140,143,80,166,142,131,27,100,177,121,12,23,127,81,90,21,53,28,46,121,37,128,31,190,113,196,151,113,18,64,156,61,119,5,102,60,171,155,20,180,199,165,88,181,171,168,151,6,151,29,25,7,169,151,7,118,195,139,153,183,17,172,105,11,100,62,27,23,48,76,162,116,66,20,12,115,198,108,116,2,133,22,159,135,123,105,161,37};
				int holeCapacity[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,6,1,0,3,0,0,0,4,0,0,1,0,0,2,3,0,2,2,0,0,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,3,0,0,0,3,0,2,1,0,1,0,5,0,1,0,0,0,1,0,0,0,0,0,0,0,5,0,0,0,4,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,2,0,4,8,4,4,0,0,0,0,0,1,0,0,0,0,0,4,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,2,0,5,1,0,2,0,3,3,0,0,0,0,0,0,0,0,0,0,2,0,3,0,2,0,0,0,3};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 8) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 300 ratos, densidade: 0.800000
				int micePosition[] = {148,200,510,391,423,71,100,570,21,15,557,129,288,87,553,358,337,580,96,553,350,290,490,113,547,480,337,363,244,290,124,289,557,358,195,336,336,283,149,289,11,303,207,207,164,555,460,148,12,415,346,585,10,538,313,133,105,127,70,6,471,50,150,87,511,282,411,492,231,227,108,228,564,506,151,291,90,99,497,149,246,432,517,283,411,553,529,198,130,483,531,477,182,19,366,445,306,468,566,110,113,302,171,88,190,375,339,124,239,500,330,444,128,389,128,60,72,599,446,265,250,406,227,437,82,206,512,169,533,390,13,220,152,79,61,69,590,518,209,200,464,151,359,164,161,259,342,484,45,271,393,548,75,335,351,341,95,330,103,65,118,2,244,323,552,328,172,94,589,319,425,75,506,2,275,305,216,99,574,154,527,314,104,148,436,560,468,482,369,383,227,128,59,164,255,476,332,86,119,247,229,98,96,60,97,587,532,225,165,363,344,525,391,541,144,547,598,157,537,390,558,360,524,193,412,330,101,471,191,95,276,477,248,370,426,302,50,85,366,409,258,183,23,399,399,337,315,75,335,264,255,340,422,25,460,79,383,322,247,328,275,242,376,290,27,528,434,594,277,127,221,223,144,174,352,69,2,531,293,3,186,158,15,479,213,407,406,462,161,321,98,510,129,529,266,81,51,158,348,577};
				int holeCapacity[] = {0,5,0,0,0,0,4,0,0,0,1,0,0,0,0,3,0,0,1,0,0,0,3,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,4,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,1,7,0,0,0,0,5,0,0,1,0,3,0,0,0,0,0,3,0,0,0,4,0,0,4,5,0,0,4,0,2,1,1,3,0,5,0,0,0,0,0,0,2,2,1,0,0,0,1,0,0,0,0,0,1,0,0,0,1,4,0,0,0,0,4,0,0,0,0,0,0,0,0,1,0,2,0,1,0,1,4,0,0,0,0,0,0,1,0,2,0,0,0,0,0,5,0,0,0,0,0,1,0,0,3,0,0,3,0,0,0,0,1,0,0,0,4,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,3,0,0,0,1,0,0,1,0,0,0,0,1,0,0,1,0,0,2,2,0,0,0,2,0,0,0,0,3,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,4,4,0,0,0,3,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,3,0,1,1,0,5,0,0,6,0,1,0,0,7,0,3,0,0,0,0,0,0,4,5,0,0,0,0,0,0,0,0,0,0,2,3,0,0,0,0,0,3,0,0,2,0,4,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,1,0,0,0,0,0,0,0,0,0,2,0,1,0,0,0,0,0,1,0,2,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,3,0,0,0,3,0,5,0,0,0,5,0,0,0,0,0,0,3,0,4,0,0,0,0,0,0,0,4,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,3,0,5,8,0,0,0,3,0,1,0,1,0,2,2,0,0,0,0,0,4,0,0,0,0,0,0,0,0,6,0,4,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,5,1,0,0,0,0,1,0,1,5,0,0,0,2,0,0,0,0,3,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,4,0,0,0,0,0,1,0,0,0,2,0,0,1,1,0,0,3,0,0,0,1,0,4,0,0,0,1,0,0,0,1,0,0,0,4,0,0,0,0,2,2,0,0,0,0,3,2,0,2,0,0,0,3,1,0,0,0,0,2,0,0,0,0,0,0,3,2,0,0,0,0,0,4,0,3,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 9) { 
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 500 ratos, densidade: 0.800000
				int micePosition[] = {968,182,93,333,33,170,263,158,59,230,468,623,164,575,546,761,769,593,82,768,771,259,562,1,452,207,630,13,890,139,24,664,255,137,574,533,371,40,851,273,178,194,398,612,559,331,966,830,248,485,774,94,786,7,223,396,538,159,973,592,664,110,528,711,569,224,267,162,904,678,874,786,768,586,297,473,926,725,243,601,993,659,57,957,762,963,580,510,872,791,425,651,929,103,70,638,918,319,367,648,474,865,311,660,164,607,544,348,546,329,738,588,977,393,585,78,376,161,354,421,21,477,730,38,902,689,524,790,2,815,927,656,664,155,934,664,171,533,720,545,511,821,380,495,811,224,848,669,581,93,46,390,453,396,149,825,712,138,233,57,610,861,856,786,780,618,884,507,833,136,60,461,360,260,914,302,247,552,326,845,909,998,733,735,605,852,151,214,712,91,160,444,343,722,387,633,28,875,893,164,979,749,29,632,408,820,13,648,554,732,603,555,931,569,244,834,637,854,950,657,948,892,303,182,698,815,493,118,182,751,552,81,613,3,433,883,999,637,945,369,332,165,933,550,615,303,646,975,387,220,879,898,258,832,757,4,346,134,182,905,441,300,489,151,726,948,472,165,160,744,554,291,710,638,916,5,2,698,745,458,304,820,914,649,754,947,964,605,126,14,943,600,569,951,60,667,953,338,841,551,800,967,162,951,97,228,825,830,419,524,229,784,325,823,427,542,901,336,53,559,198,590,291,872,542,555,80,692,291,163,869,312,977,668,1,910,978,448,586,911,252,668,93,749,460,664,44,516,209,537,522,206,138,354,258,711,997,731,691,659,677,238,528,217,25,786,545,897,541,444,888,852,831,702,55,276,640,775,759,625,307,411,109,583,763,538,603,865,937,14,863,839,77,779,340,864,358,321,907,486,710,889,873,372,120,244,19,323,662,796,222,566,42,671,887,507,118,112,608,206,519,103,215,254,996,245,671,99,811,762,383,243,906,635,535,69,542,726,905,186,801,144,87,308,479,523,215,111,768,319,222,267,245,323,199,233,229,701,306,94,833,445,853,191,984,882,685,465,624,128,219,406,121,505,408,220,755,129,927,808,71,60,739,717,16,629,425,703,523,643,302,416,838,406,704,998,481,133,359,550};
				int holeCapacity[] = {0,0,0,0,1,0,0,0,4,0,0,3,1,1,5,0,0,4,0,2,0,0,0,0,0,0,4,0,0,1,0,1,0,0,3,0,0,0,0,0,2,0,3,3,0,0,0,0,3,0,1,0,0,0,5,0,0,0,0,1,0,0,0,0,0,0,3,0,2,2,0,1,0,0,3,0,0,0,0,1,2,0,0,0,2,0,0,0,0,0,0,0,1,0,1,0,1,0,0,1,1,0,0,0,0,0,3,1,0,0,0,2,0,0,0,0,0,0,0,2,0,1,0,0,0,2,0,0,0,0,2,3,0,0,0,0,4,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,0,3,2,0,0,5,0,3,3,0,0,2,0,0,0,0,0,0,0,0,1,0,0,5,2,0,0,1,0,2,0,0,0,0,1,0,1,0,3,0,0,0,0,2,0,3,0,0,0,0,0,0,0,1,0,3,0,0,0,0,0,0,0,0,0,6,1,0,0,0,1,1,3,1,0,0,0,0,0,0,0,0,2,0,0,0,0,3,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,1,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,4,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,4,0,1,2,0,0,2,0,0,3,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,1,1,0,0,0,2,4,0,0,3,3,0,0,0,3,0,0,0,2,0,0,0,0,0,3,0,0,0,0,2,0,0,0,0,0,2,0,0,0,0,0,0,0,5,0,2,5,0,0,0,5,0,0,5,6,0,0,1,2,4,0,3,0,0,5,0,0,0,1,0,0,0,0,0,2,0,2,0,0,0,0,2,2,0,0,0,0,0,0,4,0,0,0,0,0,0,6,0,0,5,4,0,3,0,0,0,0,6,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,5,0,3,2,0,0,0,0,0,5,0,1,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4,0,0,0,3,0,0,0,0,6,1,0,0,0,0,0,3,0,0,0,0,0,2,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,3,0,3,0,0,4,0,0,0,1,0,0,0,0,1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,7,3,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,3,0,2,0,2,4,0,0,0,0,0,0,0,0,0,2,1,0,0,0,6,0,0,0,0,5,0,0,0,0,0,0,6,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,3,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,5,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,4,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,4,0,2,0,6,0,0,0,3,0,0,1,0,0,0,0,0,0,2,0,0,7,0,1,0,2,0,0,1,1,0,0,1,3,0,0,0,1,0,0,5,0,0,4,0,2,0,0,6,0,4,0,0,3,0,0,0,0,0,0,2,0,0,0,0,0,0,0,4,0,0,0,4,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,3,0,4,0,2,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,2,0,0,0,3,0,1,0,0,0,0,0,2,0,1,0,0,0,1,0,0,2,2,0,0,2,0,0,0,3,0,2,0,4,4,0,0,0,0,4,0,0,0,0,0,2,5,0,4,0,2,0,0,0,5,0,0,0,2,0,0,0,4,2,2,0,0,0,0,0,0,0,0,0,0,2,2,2,0,4,0,0,1,0,0,3,0,1,0,0,1,1,0,0,0,0,0,0,0,0,4,0,0,0,0,0,1,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,0,2,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 10) {
				System.out.println("\nh"+heuristica+"080 Cenario "+cenario);
				//Gerado cenario 1000 ratos, densidade: 0.800000
				int micePosition[] = {1661,1310,987,641,639,922,1553,1616,1840,550,1415,1568,1750,295,156,181,1226,1346,1496,1081,1449,1094,1293,1675,440,1219,1626,1191,1296,181,1707,372,1415,402,76,1771,1118,77,1583,795,1260,1322,1160,1778,1827,467,131,1093,1554,742,663,1548,16,1834,1630,819,1592,830,1755,95,503,1760,170,1619,248,795,363,1381,226,99,1378,961,720,42,74,417,1033,1116,584,1034,782,398,1987,975,1184,695,1813,311,771,766,371,1154,1982,1432,468,1599,165,1815,1027,1700,1695,179,1024,949,1301,1958,1527,1567,764,1176,1337,272,4,1193,634,1057,610,156,182,260,952,1628,1321,1365,1509,1899,294,1314,372,1539,1556,1810,1740,1242,176,1068,1436,889,508,1028,1215,452,724,367,1799,1242,1405,339,1983,1787,1771,1216,1997,1538,1368,172,1984,1348,1519,1103,1269,329,1000,113,900,180,1524,622,1625,1774,1626,1916,1135,251,174,70,803,1291,1342,1995,1529,1377,116,530,1090,414,1153,225,1760,1358,1158,572,1150,1305,1180,1439,1780,600,806,1069,447,1312,1287,662,1415,448,1435,476,1600,1772,101,406,1224,400,284,693,59,1520,75,707,222,1658,1369,327,1055,1849,1426,478,1829,217,1725,943,837,1851,91,1541,1915,1026,148,1152,467,300,978,1031,893,1496,610,1442,1727,1926,1037,502,873,680,341,140,1180,1386,1053,461,1401,1119,786,1581,1562,1073,45,498,372,15,505,106,454,1006,1432,1623,978,59,1645,581,1507,1564,1648,1073,1303,953,1706,161,97,409,1817,76,922,317,1982,271,1151,591,655,1749,933,349,1480,41,1816,1350,142,269,1126,1758,1998,1854,619,1123,941,745,607,1602,1199,380,1241,1232,125,1983,1218,55,1442,553,1974,1745,688,10,660,42,950,635,198,1748,20,794,1975,1278,1655,1750,627,28,1508,1486,1912,496,1100,1267,279,946,1314,1922,498,108,1330,310,815,309,1359,1660,1959,1595,1689,1231,1033,1711,89,1955,1882,534,731,1615,1291,1303,1416,1906,236,1791,176,1678,805,125,510,700,635,1629,1496,773,716,810,1735,1491,1987,951,1285,1682,1660,1027,295,1788,98,1538,1887,968,1614,476,1684,1638,51,938,83,177,1196,1087,1086,1590,407,1794,315,1612,141,629,1970,1541,222,329,1641,1135,1943,1466,1539,1907,60,1717,12,447,1243,1614,1998,112,1647,235,1936,1457,1968,69,1131,990,1663,1623,648,1712,805,1671,1847,1845,531,854,471,1605,1271,797,785,1648,1370,245,391,1322,276,493,592,180,521,1002,920,1985,1119,1069,242,1605,1655,224,1985,1218,191,1046,1294,1413,1933,692,747,1385,339,816,888,991,235,1169,422,867,622,882,175,1000,1793,1348,813,1853,427,1904,1063,1087,931,109,1948,220,206,1091,1938,709,801,803,1148,738,1657,1653,1764,1317,1369,1106,861,217,284,622,765,1120,987,1104,1185,552,1645,1497,1259,107,1270,1041,614,1411,1787,976,1767,200,1380,1716,60,1361,1070,1131,1951,692,1057,429,765,262,1594,47,1945,1641,606,1579,1659,1729,1093,1222,236,761,1452,1254,40,1463,147,950,480,1915,1008,1855,1586,709,995,1894,203,1319,736,62,1586,198,165,1047,80,1069,853,431,930,1361,1165,511,102,744,775,1463,1111,903,734,1172,97,121,1201,1971,42,1359,1762,772,150,1467,846,688,1108,858,1231,728,317,40,387,1624,864,1251,850,985,491,1256,1118,21,240,1199,249,1223,1459,247,1581,397,335,824,673,797,1303,1310,365,418,1711,887,1572,1088,224,195,405,348,1180,1432,1030,1159,1587,1628,972,305,654,502,1624,29,330,1076,1892,722,1237,316,559,1508,26,1536,991,247,907,623,1641,1112,1702,426,1180,1731,1314,529,1879,713,410,254,1938,840,150,1480,1652,1349,27,625,771,1654,564,1290,125,1259,1709,1113,1895,653,411,1361,822,711,315,1357,1187,1809,1129,413,156,1595,1019,1756,434,1873,582,933,1674,778,1659,196,640,1136,1,1735,259,1742,210,1535,443,1244,1151,933,132,32,188,1795,1862,489,1665,265,100,1688,1055,863,312,512,1011,59,1796,754,1080,1393,1635,1775,12,238,1499,1296,379,983,619,863,119,790,1311,268,1009,1028,949,253,858,1684,958,602,1431,697,1691,336,105,904,223,1248,1425,111,572,1395,1056,282,1851,704,946,1620,1056,1288,1003,170,1,819,1746,1281,181,1554,1558,1887,120,1184,1974,686,105,219,1655,1929,1131,184,1021,126,1362,1702,556,60,1644,469,925,1258,1250,1935,1345,1955,485,1339,1906,8,36,1857,764,1684,712,1701,827,887,1832,1264,370,1500,1874,1182,216,311,1736,707,1172,1736,276,939,1986,1186,681,1878,291,584,660,500,538,763,1935,1019,1466,1840,855,291,172,208,864,674,1997,1384,1562,344,1009,1184,777,1118,1990,830,1113,345,1361,1392,1764,218,1425,707,727,83,1799,1006,550,1061,85,1077,1593,1279,1062,1228,413,905,1592,1441,1584,1633,1892,301,460,164,386,1641,1563,1429,71,1480,364,1783,422,1737,1639,36,1468,840,269,486,1381,296,851,256,424,275,868,893,1971,546,255,372,1729,1309,1410,1735,569,622,272,1559,278,961,653,1963,1319,692,510,953,1220,1371,455,1181,829,1954,1743,1291,717,498,406,1884,1819,1316};
				int holeCapacity[] = {0,0,0,2,1,0,0,2,1,0,0,2,1,1,0,3,0,0,0,0,0,0,0,0,0,3,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,4,0,0,0,0,1,0,0,0,3,0,0,0,4,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,4,0,0,0,0,3,2,0,0,0,0,1,3,0,0,0,0,0,0,1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,0,0,0,0,0,4,2,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,4,0,0,0,0,0,3,0,0,0,0,0,1,1,0,0,0,0,0,0,0,6,0,0,0,0,3,0,0,0,0,0,0,0,5,0,2,0,0,2,0,0,0,0,0,0,2,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,2,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,2,4,0,0,0,3,0,2,0,1,2,3,1,0,0,0,5,0,0,5,0,0,0,2,0,6,0,0,0,0,0,0,0,0,0,0,0,6,0,3,0,0,0,0,2,0,0,0,0,0,0,1,5,0,0,0,0,0,0,0,0,0,0,0,0,1,4,0,0,0,0,0,0,0,0,0,2,0,0,0,2,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,5,0,3,0,5,0,1,0,0,0,0,3,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,2,0,2,3,0,0,0,0,3,0,0,0,0,4,0,0,0,1,0,0,0,0,0,0,3,0,0,3,3,0,0,0,1,0,0,2,0,0,0,0,0,2,0,0,0,0,0,4,0,0,0,0,0,0,0,2,0,2,1,1,0,0,0,0,0,0,0,4,4,0,3,0,0,1,0,0,0,0,0,2,0,4,2,0,2,1,3,0,4,0,0,0,0,1,0,1,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,2,0,0,0,4,5,0,0,4,3,2,5,0,0,0,0,0,0,2,0,0,0,0,0,0,2,0,0,0,0,1,0,0,0,0,5,0,4,0,0,0,2,2,0,0,4,0,8,0,0,2,3,0,0,0,0,0,1,0,0,4,0,0,0,0,0,0,0,1,0,0,2,2,0,0,0,0,2,0,0,0,1,1,0,0,0,0,0,0,3,1,0,0,0,0,0,1,0,0,0,0,0,2,3,4,0,0,0,0,4,0,0,5,0,4,0,0,0,0,3,0,0,5,2,0,6,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,3,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,3,0,0,2,0,2,0,0,0,3,0,0,0,0,0,0,0,0,2,0,0,5,0,0,0,4,0,3,1,1,0,1,0,0,1,0,0,0,0,0,0,0,6,0,0,5,3,0,3,0,0,5,0,0,0,0,0,0,0,0,0,1,2,0,1,0,1,2,0,0,0,4,0,0,0,2,1,1,2,1,0,0,0,0,1,1,0,0,0,0,0,2,0,1,0,0,0,3,1,2,0,0,0,0,5,0,0,0,1,0,0,5,0,0,1,0,3,0,0,0,0,1,0,1,0,0,0,1,0,0,2,2,0,0,0,0,0,0,4,0,0,0,0,0,0,0,2,0,0,0,0,3,0,0,0,6,0,0,0,0,1,0,0,0,4,1,0,0,0,0,0,0,0,0,4,0,3,0,0,2,0,0,0,0,1,0,0,0,8,0,1,0,0,3,0,3,0,0,0,2,0,0,0,0,0,1,0,5,3,0,0,0,1,0,0,0,3,0,1,0,0,1,0,0,0,0,0,0,4,0,0,0,4,2,0,0,0,0,0,0,0,0,4,0,0,0,1,1,2,0,1,0,3,0,0,0,0,0,0,1,0,0,1,0,0,5,0,0,0,0,0,4,1,0,0,1,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,1,0,4,0,0,0,3,0,0,0,0,0,0,7,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,2,0,1,0,0,0,0,0,2,0,0,0,0,1,0,0,0,0,0,1,0,0,2,0,0,3,0,3,4,1,0,0,0,4,0,0,0,0,0,0,7,0,4,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,3,2,3,0,0,3,0,0,4,0,0,0,0,0,0,0,0,1,4,1,2,0,0,1,2,3,0,0,3,0,0,6,3,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,4,6,0,0,2,0,0,0,0,4,0,0,1,0,0,0,0,0,0,4,3,0,0,0,4,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,6,3,0,2,2,0,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,1,3,0,0,0,0,0,2,4,0,1,2,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,4,0,3,3,2,1,0,0,0,1,0,4,0,0,0,0,0,0,0,0,4,0,1,3,2,1,0,0,0,0,0,3,0,0,0,4,0,0,0,2,0,0,0,0,0,0,0,3,1,2,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,3,0,0,0,3,0,0,0,0,0,0,0,0,0,2,0,3,2,3,2,3,0,0,0,0,0,0,1,0,0,0,1,0,0,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,0,0,0,0,5,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,5,1,0,0,0,2,4,0,0,1,0,0,0,1,0,5,2,4,0,0,2,0,0,0,1,1,2,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,8,0,0,0,3,3,0,4,0,0,0,0,2,0,0,3,2,0,0,0,0,0,0,2,2,0,0,0,0,6,0,0,0,0,3,0,2,0,0,0,0,2,0,0,0,2,2,0,2,0,0,0,4,0,0,3,4,0,0,0,0,0,0,4,2,0,0,0,0,1,0,0,2,0,0,0,0,0,1,0,0,2,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,4,0,0,0,1,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,4,0,0,0,0,0,3,2,0,6,0,0,6,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,2,0,0,0,0,0,0,0,0,1,0,0,4,0,0,0,0,0,0,3,0,0,0,0,1,0,4,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,5,0,3,1,1,0,1,0,0,0,0,1,0,1,0,0,1,5,0,0,0,0,0,0,0,0,3,0,0,3,1,1,1,1,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,3,2,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,6,0,0,5,4,1,2,3,1,0,0,0,0,0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,4,0,0,0,0,0,0,0,4,0,0,0,2,0,0,0,1,4,0,0,0,1,0,0,7,0,0,5,0,0,1,4,0,1,4,0,0,2,0,0,0,0,0,0,0,2,0,3,0,0,5,0,0,0,0,0,2,0,0,0,0,0,0,2,5,0,0,0,0,0,0,2,0,2,0,2,0,0,0,0,0,0,0,1,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,1,3,0,0,0,1,2,4,0,0,4,0,0,0,0,3,6,0,0,4,0,0,3,0,1,5,0,0,2,4,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,5,0,0,1,2,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,6,4,1,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,5,0,2,0,0,0,3,0,5,0,0,0,0,0,1,0,0,0,0,0,0,0,0,4,0,0};		
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			}
		}
	}	

	private static void rodarCenariosDensidade_100(int selecao, int heuristica) throws Exception {
		int cenario, cenarioFinal;

		if (selecao == -1) {
			cenario = 0;
			cenarioFinal = MAX_CENARIO;
		} else {
			cenario = selecao-1;
			cenarioFinal = selecao;
		}
		
		//Garante que todos os algoritmos iniciam ativos
		for (int i = 0; i < 5; i++) algoritmosAtivos[i] = true;

		while (++cenario <= cenarioFinal) {
			if (cenario == 1){
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 4 ratos, densidade: 1.000000
				int micePosition[] = {1,4,1,2};
				int holeCapacity[] = {0,2,0,0,2,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 2) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 5 ratos, densidade: 1.000000
				int micePosition[] = {9,1,10,7,3};
				int holeCapacity[] = {0,0,2,0,2,0,0,0,0,1};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 3) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 6 ratos, densidade: 1.000000
				int micePosition[] = {10,6,1,11,7,9};
				int holeCapacity[] = {0,0,1,0,0,0,2,0,2,0,1,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 4) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 8 ratos, densidade: 1.000000
				int micePosition[] = {2,3,8,13,9,13,16,14};
				int holeCapacity[] = {0,2,0,0,3,0,0,0,0,0,0,0,0,0,0,3};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 5) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 10 ratos, densidade: 1.000000
				int micePosition[] = {3,13,4,9,8,2,14,16,8,17};
				int holeCapacity[] = {2,0,0,1,0,1,0,0,0,0,4,0,0,2,0,0,0,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 6) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 20 ratos, densidade: 1.000000
				int micePosition[] = {32,35,32,24,16,33,34,36,39,30,26,33,37,22,4,28,18,27,27,1};
				int holeCapacity[] = {0,1,0,0,0,0,0,0,1,0,0,5,0,0,0,0,2,0,0,0,7,0,0,0,0,0,0,0,0,0,0,1,0,0,0,3,0,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 7) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 100 ratos, densidade: 0.990099
				int micePosition[] = {121,29,9,62,24,52,21,33,179,61,128,4,63,53,61,157,137,28,60,39,189,82,150,190,113,97,188,171,177,91,110,68,79,138,124,143,153,87,21,46,157,126,50,14,126,14,152,141,142,88,110,178,163,27,34,52,94,183,154,195,23,197,181,135,132,123,85,176,116,108,62,50,178,159,174,198,32,195,69,186,128,53,23,148,146,80,37,104,198,24,122,141,138,125,99,35,118,32,131,14};
				int holeCapacity[] = {0,2,0,0,0,0,1,0,4,0,0,0,0,0,4,0,0,0,2,1,3,0,0,0,0,1,0,0,0,1,0,1,0,0,0,0,0,1,0,2,0,0,0,1,0,0,3,0,0,0,1,0,2,0,0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,0,0,3,0,0,3,2,2,0,0,0,2,0,0,0,0,0,0,0,0,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,0,0,0,0,2,0,6,0,0,2,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,1,0,0,4,1,0,4,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,8,0,0,0,0,0,1,0,0,0,0,0,3,0,0,0,0,3,0,1,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 8) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 300 ratos, densidade: 0.990099
				int micePosition[] = {291,112,579,79,28,218,334,27,148,529,213,238,270,384,283,331,279,68,13,292,20,152,490,504,369,415,66,437,535,260,521,12,335,529,104,459,581,327,21,434,26,440,554,412,488,7,90,447,390,503,475,326,176,287,184,249,343,515,263,456,417,214,222,471,150,8,164,44,317,476,321,171,482,411,139,251,3,349,36,153,58,299,524,569,383,551,442,467,287,487,556,503,217,375,349,527,363,442,153,318,537,14,304,200,276,205,547,186,268,180,569,497,98,18,576,544,402,583,139,479,546,574,161,322,568,378,244,486,504,190,565,384,215,178,565,295,238,394,219,389,168,482,66,190,161,550,479,487,334,433,245,97,56,250,363,249,494,41,590,166,445,272,7,434,89,496,550,272,135,153,12,341,83,150,307,306,531,208,326,228,71,433,532,552,255,25,558,426,304,83,289,288,547,517,206,426,263,534,466,39,304,213,521,589,539,3,544,343,118,83,376,333,93,136,520,494,144,507,379,158,372,87,415,167,410,214,516,359,433,51,373,319,278,123,486,242,173,552,379,379,219,45,5,497,230,291,416,581,74,153,444,25,232,222,424,508,572,391,155,237,557,143,209,555,378,162,572,98,310,443,553,280,393,480,510,193,326,302,237,392,166,140,275,512,372,596,516,73,572,388,431,65,341,187,294,44,293,424,409,363};
				int holeCapacity[] = {0,1,0,0,0,1,0,0,1,0,0,1,0,0,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,6,0,6,0,0,0,0,0,1,0,0,1,0,0,0,0,5,0,0,0,0,3,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,2,0,0,0,1,3,0,0,0,0,0,0,0,2,0,0,0,2,0,3,0,0,0,0,4,0,4,0,0,0,4,2,1,0,0,0,3,0,1,2,0,2,0,0,0,0,0,5,0,0,0,0,0,2,0,4,0,2,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,2,1,1,4,0,0,0,4,0,0,0,0,0,0,3,3,0,0,0,1,0,0,0,0,0,0,0,3,0,1,0,0,0,0,0,0,0,0,0,0,1,3,0,0,0,4,0,0,0,0,3,0,0,0,0,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,3,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1,0,1,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,2,0,0,0,0,1,0,1,2,0,0,0,0,0,0,4,0,1,1,0,0,0,0,0,2,0,1,6,0,0,0,0,0,0,0,0,5,0,1,5,0,0,0,0,0,0,0,0,0,5,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,0,0,0,0,7,0,0,0,0,0,0,0,1,0,2,0,0,1,2,4,0,0,0,0,0,0,0,0,0,0,0,5,3,0,0,0,0,0,1,0,2,1,0,0,0,0,0,0,0,0,0,0,2,2,0,5,2,5,0,0,0,0,0,4,5,0,0,0,3,0,0,0,0,0,0,0,0,0,3,4,4,0,0,0,0,0,0,0,0,0,0,2,1,0,0,0,0,1,0,3,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,4,0,0,0,0,2,0,0,0,1,0,3,0,0,0,0,1,5,0,0,0,0,1,0,0,0,1,0,0,0,0,0,5,0,0,0,0,3,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 9) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 500 ratos, densidade: 0.990099
				int micePosition[] = {808,575,814,745,429,111,900,942,62,233,519,566,97,630,758,252,864,790,967,779,643,476,397,993,500,99,988,781,424,713,341,22,247,459,266,515,289,890,25,720,334,91,105,379,201,922,10,687,926,500,451,202,453,925,446,122,157,347,705,561,576,606,987,640,277,434,294,804,435,895,870,508,11,817,790,13,903,966,453,417,115,691,741,7,421,686,703,221,421,620,226,512,966,700,326,127,981,829,825,796,777,346,52,719,754,732,348,65,134,944,181,158,713,564,320,243,970,614,336,220,105,874,360,587,968,833,153,260,224,658,308,389,239,28,986,58,376,640,230,964,710,576,298,250,507,266,407,339,22,255,714,691,266,331,893,72,86,31,8,744,244,97,941,355,47,503,247,799,139,119,181,538,243,37,556,984,510,342,413,662,944,198,428,204,14,592,917,487,699,89,561,18,762,239,627,512,716,14,133,545,342,294,248,519,844,366,85,907,517,285,707,836,812,551,879,284,132,688,463,713,996,266,976,921,319,983,191,69,113,225,425,419,999,235,610,988,944,956,160,549,378,425,145,115,639,871,127,31,656,963,642,24,874,626,636,794,371,249,843,320,80,177,687,51,997,484,23,434,729,885,762,902,699,447,741,905,887,334,999,623,847,50,227,805,158,837,708,159,754,361,691,767,371,847,616,48,384,987,689,798,480,269,174,974,78,574,446,275,447,624,927,944,748,624,697,573,760,13,361,43,231,148,71,880,512,247,235,592,484,444,375,503,104,719,247,325,652,563,829,401,521,664,225,72,575,105,326,745,60,430,755,512,583,805,847,975,752,451,200,250,72,413,310,281,766,835,817,654,211,464,466,378,979,859,772,658,697,558,146,444,822,205,645,546,108,700,293,339,319,944,186,278,322,512,832,647,713,266,626,521,352,383,749,725,823,538,887,256,791,194,264,195,730,546,894,143,193,285,774,227,430,81,439,44,406,369,731,345,506,103,861,613,727,58,684,734,457,699,603,716,614,316,758,799,763,774,818,878,413,451,354,842,210,172,718,109,307,1000,487,188,836,931,208,535,504,422,649,552,73,2,431,754,724,548,435,559,659,397,805,928,962,699,50,39,208,206,387,699,110,740,975,497,147,617,838,789,757,884,792,705};
				int holeCapacity[] = {0,0,0,1,0,0,0,5,0,1,0,0,1,0,0,1,8,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,3,1,0,0,2,0,2,0,0,0,0,0,0,0,1,0,0,0,6,0,0,2,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,1,0,4,0,0,1,0,1,0,0,1,0,0,0,0,3,6,4,0,0,0,0,1,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,1,0,4,0,0,0,0,1,0,0,0,0,0,0,2,0,0,0,4,0,4,0,0,4,0,0,3,0,0,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,0,0,0,6,0,0,0,0,0,0,2,1,0,0,0,0,0,0,0,3,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,1,3,3,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,2,0,0,3,0,1,2,0,0,0,0,2,0,4,0,1,0,5,5,0,0,0,1,0,1,3,0,0,0,0,3,0,0,0,0,0,4,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,6,4,5,0,0,0,3,0,2,0,0,0,0,0,0,2,1,0,0,1,0,6,2,0,1,0,0,0,0,0,1,0,2,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,5,0,0,0,0,0,2,0,0,3,0,1,0,0,0,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,2,0,4,0,0,0,0,0,0,2,0,0,0,0,2,3,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,1,0,0,0,0,0,0,3,0,3,3,0,0,0,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,2,0,0,0,0,0,4,0,0,3,0,0,0,3,3,0,2,3,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,2,0,2,4,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,3,3,3,0,2,0,0,0,1,0,0,2,0,0,0,0,0,0,0,0,0,0,3,3,3,0,1,1,0,1,0,0,0,0,0,4,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,2,0,0,4,2,7,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,1,1,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,4,2,0,0,0,1,0,0,0,0,0,6,0,2,0,5,0,0,0,0,3,0,0,0,0,0,0,0,0,1,1,0,2,0,0,0,0,0,0,0,0,0,0,3,2,0,0,0,0,3,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,2,5,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,1,1,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,3,0,0,2,0,0,3,0,0,0,0,4,1,0,4,3,3,0,5,1,0,0,0,0,2,3,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,2,0,0,0,0,0,0,0,3};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			} else if (cenario == 10) {
				System.out.println("\nh"+heuristica+"100 Cenario "+cenario);
				//Gerado cenario 1000 ratos, densidade: 0.990099
				int micePosition[] = {963,1322,1073,1049,1346,1243,1433,1153,1101,1848,1225,871,1855,416,1380,1707,863,1117,974,797,1312,1200,1450,1263,285,494,268,1694,138,1934,369,1315,744,116,27,985,1494,910,576,712,734,967,1318,286,870,1388,1083,702,1198,719,332,395,1732,752,1658,872,63,464,245,1707,1852,124,1048,1325,986,1770,695,1030,1076,603,1053,1228,1956,94,1833,595,1445,1060,1200,405,1394,1131,507,135,681,946,1674,1011,1884,667,249,1046,210,1094,727,1455,1839,753,1596,1713,754,457,1787,225,1911,1907,98,1680,1760,1145,688,129,1080,1755,895,359,1441,673,710,1185,660,1321,1669,1016,471,940,1184,1889,1124,1705,1500,1086,502,736,1774,1709,1163,577,1599,1911,1414,479,399,1929,305,167,784,1975,621,474,1026,543,1806,218,1210,1838,1248,1360,853,680,1858,444,155,6,1647,1923,854,1916,1554,214,1148,1629,1102,1644,1210,1236,678,100,1841,739,1025,1611,1309,650,1297,1484,597,1148,1464,480,1406,837,1648,380,973,1903,952,39,1162,1573,256,1383,1053,965,2000,727,1285,727,121,1897,1508,47,1368,1893,422,1619,134,1239,1711,1021,1626,1776,377,1812,152,374,1942,793,419,139,699,1157,253,514,1044,1611,753,313,1943,1811,1749,282,821,1292,981,620,496,1879,388,768,859,360,982,1297,1687,1022,439,96,1532,1924,1172,1242,878,1043,1876,1368,38,1769,736,394,560,1860,556,1788,250,1424,1656,1274,1305,1022,262,1385,1557,55,830,1906,1299,1684,1177,1328,1529,669,1320,1331,304,1528,1045,1297,1125,769,311,1236,624,1337,1819,444,1770,1523,76,1177,194,816,1166,325,1766,679,77,1374,392,1425,1970,672,6,295,225,1708,171,310,1988,77,695,655,767,966,1615,1536,886,1565,495,1708,549,1434,99,616,1565,28,1637,1285,203,1764,193,1987,1026,1575,1914,1424,1267,1551,1341,352,1644,208,1912,465,474,873,1243,1083,267,1408,721,709,978,1047,1817,1465,1369,1291,1798,1127,933,1008,1635,480,1128,654,1930,1719,307,1630,1739,1274,199,1373,1465,1390,388,1868,1498,783,884,1494,934,117,608,1451,695,459,580,456,843,491,173,1694,1641,1863,140,125,61,405,1153,519,1367,1168,1533,1263,253,1021,1461,423,20,376,1104,296,1695,941,1281,244,724,1206,928,224,1106,718,1918,560,131,1620,1866,948,1207,1240,1497,632,1617,1252,967,1098,1456,829,1651,198,1692,1962,897,1972,950,1179,189,1825,1368,406,7,1343,217,115,1060,1025,736,256,1730,329,147,847,986,75,318,545,1521,1461,187,228,31,1354,1005,1468,42,1143,984,1462,1435,1749,1835,1997,1527,502,306,819,669,1011,1276,28,80,984,1584,964,471,407,537,782,586,835,1777,165,161,1393,1832,74,47,129,852,1718,1564,1113,1539,1462,1737,1634,1486,1371,1408,1347,1824,915,1774,1417,1404,445,360,85,1765,1370,409,526,524,170,358,1087,1213,1601,648,1354,362,1614,1706,543,1191,888,1583,859,746,51,1877,1251,205,1734,1917,511,591,346,1136,620,845,1337,243,918,620,1395,919,527,1267,1375,557,1246,1338,1444,1,128,1347,1324,1719,759,1586,1771,876,794,658,1969,433,627,558,1625,243,1053,391,478,1613,1302,1148,578,1856,1213,1684,1193,471,1911,136,1658,1001,8,744,326,634,941,1459,866,1234,1341,391,281,1436,170,1832,986,1826,251,853,147,648,414,855,1058,648,726,1175,1803,1459,1444,1205,1403,158,1286,1156,409,1351,1480,664,1757,776,147,1362,1448,35,1101,330,1347,559,924,971,281,1207,847,515,1804,859,318,1215,561,1475,1777,382,810,1206,348,744,1299,261,525,1759,550,1228,1760,891,1221,1266,1491,1680,677,1071,1743,281,1271,1972,1091,1863,1775,263,1873,410,835,1611,204,515,301,1538,63,1492,1467,1970,1522,1258,1164,1967,624,1894,462,801,1567,583,1502,1614,1235,351,1909,273,1839,311,708,504,609,748,15,1303,902,678,1973,1277,173,1600,973,1821,847,645,1903,818,1753,173,1260,247,1927,1158,531,348,1755,1208,1838,1039,478,1151,1781,1181,1687,1313,556,1671,633,737,1073,1450,428,1314,1849,1078,1177,1402,1450,413,1943,77,782,962,144,1073,1367,915,685,1011,471,1630,1600,293,393,1715,920,1141,1257,885,1237,100,1146,102,677,1224,902,1016,509,1352,1149,1280,1230,1198,1136,155,1019,310,237,1874,594,1612,905,1878,1215,1522,1465,707,1688,913,810,82,286,1535,1713,1416,1718,1663,1632,1248,1725,734,739,1737,289,1408,577,1179,1424,1433,1023,945,1268,376,273,332,1758,91,115,255,901,614,1842,1908,1172,26,367,746,750,1331,623,1724,1864,1053,912,639,337,1381,960,566,44,1564,956,1856,1374,694,1592,1238,647,1356,61,1021,1089,687,841,1855,1834,1122,494,1397,1728,1603,594,1023,1266,445,1112,181,1455,100,471,937,1196,85,828,1837,1021,795,1583,1165,107,1273,25,1027,883,232,1206,771,1317,940,925,1571,978,147,493,1813,1225,1084,638,1706,1027,988,1042,715,1224,1699,25,1652,1855,182,663,1986,1611,739,863,251,677,818,1715,1898,1487,1376,818,537,1297,1211,418,507,1063,1975,1541,1597,1769,407,1450,1547,1163,1281,833,1293,1549,855,1491};
				int holeCapacity[] = {0,0,0,0,0,0,0,0,2,0,3,0,1,0,0,0,0,0,0,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,3,5,0,0,0,0,2,0,1,0,0,1,6,0,0,2,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,1,0,0,0,0,0,3,0,1,0,0,0,0,0,0,0,1,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,2,2,1,0,0,0,0,0,0,2,0,1,2,4,0,0,3,0,0,0,1,0,0,1,2,0,0,0,0,0,1,0,4,3,0,0,0,1,0,0,0,0,0,1,0,0,0,2,1,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,2,0,3,0,0,0,0,0,3,0,0,0,0,0,0,1,0,0,0,0,5,4,0,0,2,0,0,0,1,1,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,1,2,2,1,0,3,0,0,0,0,0,1,2,0,0,0,0,0,0,4,0,2,1,0,2,3,0,0,0,0,0,0,0,1,0,0,0,0,0,3,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,0,1,0,1,1,0,0,3,0,0,0,2,0,3,0,0,2,0,0,0,3,0,4,5,0,4,0,3,0,0,2,0,2,2,0,0,0,0,0,1,0,0,0,0,0,0,0,4,2,0,0,3,0,0,0,4,0,0,0,0,0,2,0,4,0,0,4,0,3,0,0,0,0,0,0,2,0,0,0,3,0,0,0,0,4,0,0,0,0,0,0,0,2,1,0,0,0,3,0,0,0,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,8,0,0,0,0,4,0,1,0,0,0,0,2,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,1,0,0,0,0,0,0,0,0,3,0,1,2,0,0,0,0,3,0,0,1,0,0,1,4,0,0,0,2,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,3,6,5,0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,1,0,1,0,1,0,0,0,0,3,0,0,1,5,0,0,2,0,0,2,0,0,0,0,2,0,1,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,2,0,6,0,0,0,0,0,0,0,0,2,0,2,0,0,0,0,2,0,0,5,0,0,0,0,0,0,2,4,0,0,0,0,0,0,0,2,0,0,0,0,0,6,1,0,0,0,0,0,1,0,0,0,4,0,0,0,0,0,0,0,3,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,1,0,0,0,0,3,0,3,3,0,0,0,0,0,0,1,3,2,0,0,1,0,0,0,0,0,0,2,0,0,0,0,9,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,2,0,2,0,0,0,0,0,0,0,3,5,0,0,0,2,1,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,3,0,0,2,0,0,0,0,0,0,0,0,5,3,0,0,2,0,0,1,0,0,0,0,1,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2,0,1,0,4,0,0,0,4,0,0,0,0,0,0,3,0,0,0,0,0,0,0,1,0,2,4,3,0,0,0,0,0,0,0,0,0,0,0,1,0,2,3,0,0,0,2,4,0,4,0,0,0,0,0,0,1,0,0,3,0,0,0,0,0,0,2,0,2,0,2,0,1,0,3,0,0,0,7,0,5,0,0,0,0,2,1,0,0,6,0,0,0,0,0,2,0,0,0,1,0,0,2,0,0,2,0,1,0,0,0,0,3,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,2,0,0,1,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,4,0,0,0,0,0,0,5,2,0,0,0,0,0,1,0,0,1,2,0,0,0,4,0,1,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,5,0,0,4,2,0,4,0,1,2,0,0,0,0,0,0,0,0,0,1,3,0,0,0,0,1,0,0,0,5,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,1,0,2,0,0,3,2,0,1,0,0,0,0,0,0,0,0,0,3,2,0,0,0,4,0,0,6,0,0,0,0,1,0,0,1,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,5,0,1,4,0,2,0,0,0,0,0,0,2,0,0,0,0,2,3,0,1,0,0,0,0,0,0,0,0,0,0,4,2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,3,0,0,0,3,0,3,0,0,0,0,0,4,0,0,0,0,2,0,0,0,0,2,0,0,1,0,0,0,1,0,0,1,0,0,4,0,0,0,5,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,4,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,2,0,8,0,5,0,3,0,0,0,0,1,0,0,0,0,0,4,0,2,0,0,4,3,0,0,0,0,0,0,0,4,0,3,0,4,0,0,0,0,0,5,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,7,0,0,2,0,0,0,4,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,2,1,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,1,0,0,0,5,0,4,0,0,4,0,0,0,1,0,0,2,2,0,0,0,0,3,1,0,0,4,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,6,0,0,0,2,0,0,0,0,0,1,2,0,0,0,0,0,0,0,0,0,0,1,0,1,1,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,6,0,0,1,3,0,0,0,2,0,0,0,0,0,0,4,0,0,0,0,0,0,1,0,0,0,2,2,0,2,0,0,1,1,3,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,2,0,0,3,0,2,0,0,4,0,0,0,0,0,2,1,0,0,0,0,0,0,1,0,0,0,0,2,0,0,0,0,5,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,3,0,0,0,0,0,0,0,0,0,3,0,0,0,2,0,2,0,0,0,0,0,0,3,1,0,0,0,0,1,0,1,4,0,3,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,4,0,1,0,0,4,0,4,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,0,0,4,0,0,0,0,1,0,0,1,0,0,0,2,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,1,0,1,1,0,0,0,0,0,1,0,5,0,0,0,0,0,0,0,4,0,2,0,0,0,0,1,1,1,0,4,0,0,0,0,1,0,2,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,2,0,0,0,0,0,0,4,2,0,0,0,0,0};
				rodarAlgoritmosh2(micePosition, holeCapacity, heuristica);
			}
		}
	}

}
