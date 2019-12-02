package CommitSearchTransaction;

import java.io.IOException;

import org.json.JSONException;

public class SearchCommitTransaction {

	public static void main(String[] args) throws IOException, JSONException {

//		CommitSearchTransaction commitSearchTransaction = new CommitSearchTransaction(); 
//		commitSearchTransaction.getAllCommitUrlReader(); /* Tüm repolarý tarayýp en çok commit atan kiþiyi bulan static metod -- Githup api rate limit 5000 sorgudan sonra*/ 

		CommitSearchTransaction.searchCommit("avelino","awesome-go");
		/* Herhangi belirli bir repodaki en çok commit atan kiþiyi bulan metod */
		// CommitSearchTransaction.searchCommit("mojombo", "grit");
		//  /* Rate limit  uðramamak için el ile seçtiðim birden fazla repoya sorguya atarak iki repoya
		// commit atan kiþinin toplam commit sayýsýný buluyoruz.*/
		
		
		
		
//		 CommitSearchTransaction.example();  /*Ýlk yapýlan kendi repomdaki repoya atýlan commit miktarý */
	}
}
