 package com.sirius.library.mobile;




 import com.google.gson.Gson;
 import com.sirius.library.mobile.models.KeyDidRecord;
 import com.sirius.library.mobile.utils.FileUtils;

 import org.hyperledger.indy.sdk.IndyException;
 import org.hyperledger.indy.sdk.did.Did;
 import org.hyperledger.indy.sdk.did.DidResults;
 import org.hyperledger.indy.sdk.non_secrets.WalletRecord;
 import org.hyperledger.indy.sdk.pool.PoolJSONParameters;
 import org.hyperledger.indy.sdk.wallet.Wallet;
 import org.hyperledger.indy.sdk.wallet.WalletAccessFailedException;
 import org.hyperledger.indy.sdk.wallet.WalletNotFoundException;

 import java.io.File;
 import java.security.NoSuchAlgorithmException;
 import java.security.spec.InvalidKeySpecException;
 import java.util.concurrent.ExecutionException;



 public class IndyWallet {
     private static final String dirPath = File.separator + "wallet";
     public static final String genesisPath = File.separator + "genesis";
     public static final int OPEN_WALLET_REQUEST_CODE = 1007;
     public static final int SCAN_INVITATION_WALLET_REQUEST_CODE = 1009;

    public static  void initialize(){

    }
     public static Wallet getMyWallet() {
         return myWallet;
     }

     public static Wallet myWallet;


 /*
     public static  void generateMyDIDOnce(){
         if( getMyDid().getDid()==null){
             createAndStoreMyDid(getMyWallet());
         }
     }
 */


   /*  public static DidResults.CreateAndStoreMyDidResult getMyDid(){

         DidResults.CreateAndStoreMyDidResult result = new DidResults.CreateAndStoreMyDidResult();
     }*/

  /*   public static String KeyForMe(Wallet wallet, String key) {

     }*/
     public static String DIDForKey(Wallet wallet, String key) {
         String did = null;
         try {
             did = WalletRecord.get(wallet, "key-to-did", key, "{}").get();
             System.out.println("mylog900 DIDForKey key=" + key + " did=" + did);
             if (did != null) {
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(did, KeyDidRecord.class);
                 return didRecord.getValue();

             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
         return did;
     }


     public static String DIDForRecipentKey(Wallet wallet, String key) {
         String did = null;
         try {
             String didJson = WalletRecord.get(wallet, "recipentkey-to-did", key, "{}").get();
             System.out.println("mylog900 DIDForKey key=" + key + " didJson=" + didJson);
             if (didJson != null) {
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(didJson, KeyDidRecord.class);
                 return didRecord.getValue();

             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
         return did;
     }

     public static void removeDIDForRecipentKey(Wallet wallet, String key, String did) {
         try {
             System.out.println("mylo540 key=" + key + " did=" + did);
             WalletRecord.add(wallet, "recipentkey-to-did", key, did, "{}").get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }

     }

     public static void addOrUpdateDIDForRecipentKey(Wallet wallet, String key, String did) {
         try {
             System.out.println("mylo540 key=" + key + " did=" + did);
            String didFor =  DIDForRecipentKey(wallet, key);
             if (didFor == null || didFor.isEmpty()) {
                 WalletRecord.add(wallet, "recipentkey-to-did", key, did, "{}").get();
             } else {
                 WalletRecord.updateValue(wallet, "recipentkey-to-did", key, did).get();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }

     }


     public static DidResults.CreateAndStoreMyDidResult createAndStoreMyDid(Wallet wallet) {
         try {
             DidResults.CreateAndStoreMyDidResult myDidResult = Did.createAndStoreMyDid(wallet, "{}").get();

             String key = myDidResult.getVerkey();
             String did = myDidResult.getDid();
             WalletRecord.add(wallet, "key-to-did", key, did, "{}").get();
             System.out.println("mylog900 createAndStoreMyDid key=" + key + " did=" + did);
             return myDidResult;
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }
         return null;
     }


     public static void createAndStoreTheirDid(Wallet wallet, String theirDid, String theirKey) {
         try {
             String theirJson = "{\"did\":\"" + theirDid + "\"" +
                     ",\"verkey\":" + "\"" + theirKey + "\"" + "}";

              String didForKey = DIDForKey(wallet,theirKey);
             if(didForKey==null){
                 WalletRecord.add(wallet, "key-to-did", theirKey, theirDid, "{}").get();
                 System.out.println("mylog900 createAndStoreTheirDid theirJson=" + theirJson);
             }else{
                 WalletRecord.updateValue(wallet, "key-to-did", theirKey, theirDid).get();
                 System.out.println("mylog900 createAndStoreTheirDid updateValue theirJson=" + theirJson);
              }
             Did.storeTheirDid(wallet, theirJson).get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
     }



     public static String getCredDefMessage(String credDefId) {
         try {
             String values = WalletRecord.get(IndyWallet.getMyWallet(), "cred_def_message", credDefId, "{}").get();
             if (values != null) {
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(values, KeyDidRecord.class);
                 return didRecord.getValue();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }

     public static void deleteCredDefMessage(String credDefId) {
         String defMess = getCredDefMessage(credDefId);
         if (defMess!=null && !defMess.isEmpty()) {
             try {
                 WalletRecord.delete(IndyWallet.getMyWallet(), "cred_def_message", credDefId).get();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (IndyException e) {
                 e.printStackTrace();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
     }


     public static void storeCredDefMessage(String credDefId, String metaData) {
         deleteCredDefMessage(credDefId);
         try {
             WalletRecord.add(IndyWallet.getMyWallet(), "cred_def_message", credDefId, metaData, "{}").get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e) {
             e.printStackTrace();
         }
     }


     public static void storeCredDefMessage2(String credDefId, String metaData) {
         deleteCredDefMessage2(credDefId);
         try {
             WalletRecord.add(IndyWallet.getMyWallet(), "cred_def_message2", credDefId, metaData, "{}").get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
     }

     public static void deleteCredDefMessage2(String credDefId) {
         String message = getCredDefMessage2(credDefId);
         if (message!=null && !message.isEmpty()) {
             try {
                 WalletRecord.delete(IndyWallet.getMyWallet(), "cred_def_message2", credDefId).get();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (IndyException e) {
                 e.printStackTrace();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
     }


     public static String getCredDefMessage2(String credDefId) {
         try {
             String values = WalletRecord.get(IndyWallet.getMyWallet(), "cred_def_message2", credDefId, "{}").get();
             if (values != null) {
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(values, KeyDidRecord.class);
                 return didRecord.getValue();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }




     public static void storeIssuerSchema(String schemaId, String body) {
         deleteIssuerSchema(schemaId);
         try {
             WalletRecord.add(IndyWallet.getMyWallet(), "wallet_issuer_schema", schemaId, body, "{}").get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
     }

     public static void deleteIssuerSchema(String schemaId) {
       String schema =   getIssuerSchema(schemaId);
         if (schema!=null && !schema.isEmpty()) {
             try {
                 WalletRecord.delete(IndyWallet.getMyWallet(), "wallet_issuer_schema", schemaId).get();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (IndyException e) {
                 e.printStackTrace();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
     }

     public static String getIssuerSchema(String schemaId) {
         try {
             String values = WalletRecord.get(IndyWallet.getMyWallet(), "wallet_issuer_schema", schemaId, "{}").get();
             if (values != null) {
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(values, KeyDidRecord.class);
                 return didRecord.getValue();
             }
         } catch (IndyException e) {
             e.printStackTrace();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }

     public static void storeCredDef(String credDefId, String metaData) {
         System.out.println("mylog2080 storeCredDef credDefId="+credDefId);
         deleteCredDef(credDefId);
         try {
             WalletRecord.add(IndyWallet.getMyWallet(), "cred_def_key", credDefId, metaData, "{}").get();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
     }


     public static String getCredDef(String credDefId) {
         System.out.println("mylog2080 getCredDef credDefId="+credDefId);
         try {
             String values = WalletRecord.get(IndyWallet.getMyWallet(), "cred_def_key", credDefId, "{}").get();
             if (values != null) {
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(values, KeyDidRecord.class);
                 return didRecord.getValue();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (IndyException e) {
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }

     public static void deleteCredDef(String credDefId) {
         String value = getCredDef(credDefId);
         if (value != null) {
             try {
                 WalletRecord.delete(IndyWallet.getMyWallet(), "cred_def_key", credDefId).get();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (IndyException e) {
                 e.printStackTrace();
             }catch (Exception e){
                 e.printStackTrace();
             }
         }
     }

     public void isDirEmpty(File projDir) {
         if (projDir.isDirectory()) {
             String[] listFiles = projDir.list();
             if (listFiles != null) {
                 for (String name : listFiles) {
                     new File(name);
                 }
             }
         } else {

         }

     }

    public static Wallet openWallet(String userJid,String pin) throws WalletNotFoundException, WalletAccessFailedException, IndyException, InvalidKeySpecException, NoSuchAlgorithmException, ExecutionException, InterruptedException {

        //TODO REFACTOR WITH LAYERS
         String alias = HashUtils.generateHash(userJid);
         File projDir = new File(dirPath);
         if (!projDir.exists()) {
             projDir.mkdirs();
         }
         String path = "{\"path\":\"" + projDir + "\"}";
         String pass = HashUtils.generateHashWithoutStoredSalt(pin, alias);
         String myWalletConfig = "{\"id\":\"" + alias + "\" " + ", \"storage_config\":" + path + "}";
         String myWalletCredentials = "{\"key\":\"" + pass + "\"}";
         return Wallet.openWallet(myWalletConfig, myWalletCredentials).get();

     }

/*     public static void deleteWallet() {
         try {
             //TODO REFACTOR WITH LAYERS
             IndyWallet.closeMyWallet();
             String alias = HashUtils.generateHash(AppPref.getUserJid());
             Log.d("mylog2080","deleteWallet dirPath + File.separator + alias="+dirPath + File.separator + alias);
             File projDir = new File(dirPath + File.separator + alias);
             FileUtils.cleanDirectory(projDir);
             FileUtils.deleteDirectory(projDir);
         } catch (Exception e) {
             Log.d("mylog2080","deleteWallet dirPath + File.separator + alias="+e.getMessage());
             e.printStackTrace();
         }

     }*/

     public static void deleteWallet(String userJid) {
         try {

             String alias = HashUtils.generateHash(userJid);
             File projDir = new File(dirPath + File.separator + alias);
             FileUtils.cleanDirectory(projDir);
             FileUtils.deleteDirectory(projDir);
         } catch (Exception e) {
             e.printStackTrace();
         }

     }


     public static void closeWallet(Wallet wallet) throws WalletNotFoundException, WalletAccessFailedException, Exception {
         wallet.closeWallet().get();
         myWallet = null;
     }

     public static void closeMyWallet() {
         try {
             if (myWallet != null) {
                 myWallet.closeWallet().get();
                 myWallet = null;
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         myWallet = null;
     }


     public static String createPoolLedgerConfigFromString(String string) {

         PoolJSONParameters.CreatePoolLedgerConfigJSONParameter createPoolLedgerConfigJSONParameter
                 = new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(string);
         return createPoolLedgerConfigJSONParameter.toJson();

     }



 //TODO REFACTOR OR DELETE
    /* public static Wallet getOrOpenMyWallet(Activity activity, int extra) {
         if (myWallet != null) {
             return myWallet;
         } else {
             openWalletWithCode(activity, extra);
         }
         return null;
     }

     public static Wallet getOrOpenMyWalletNew(Activity activity, int extra) {
         if (myWallet != null) {
             return myWallet;
         } else {
             openWalletWithCodeNew(activity, extra);
         }
         return null;
     }

     public static Wallet getOrOpenMyWallet(Fragment fragment, int extra, String strinExtra) {
         if (myWallet != null) {
             return myWallet;
         } else {
             openWalletWithCode(fragment, extra, strinExtra);
         }
         return null;
     }

     public static Wallet getOrOpenMyWallet(Fragment fragment, Bundle options) {
         if (myWallet != null) {
             return myWallet;
         } else {
             openWalletWithCode(fragment, options);
         }
         return null;
     }
 */

 /*    public static void setUserResourses() {
         String userSessionId = AppPref.getInstance().getUserResourses();
         Log.d("mylog900", "setUserResourses from Prefs userSessionId=" + userSessionId);
         if(TextUtils.isEmpty(userSessionId)){
             try {
                 String userSessionIdJson = WalletRecord.get(myWallet, "user_session_id", "my_session", "{}").get();
                 Gson gson = new Gson();
                 KeyDidRecord didRecord = gson.fromJson(userSessionIdJson, KeyDidRecord.class);
                 userSessionId = didRecord.getValue();
                 Log.d("mylog900", "setUserResourses from Wallet userSessionId=" + userSessionId);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (IndyException e) {
                 e.printStackTrace();
             } catch (Exception e) {
                 e.printStackTrace();
             }

             try {
                 if (TextUtils.isEmpty(userSessionId)) {
                     userSessionId = UUID.randomUUID().toString();
                     WalletRecord.add(myWallet, "user_session_id", "my_session", userSessionId, "{}").get();
                 }
                 Log.d("mylog900", "setUserResourses generateNew =" + userSessionId);
             } catch (Exception e) {
                 e.printStackTrace();
             }
             AppPref.getInstance().setUserResourses(userSessionId);
         }
     }
*/
     //TODO REFACTOR
    /* public static void openWalletWithCode(Activity activity, int extra) {
         WalletCodeActivity.startWalletCodeActivityForResult(activity, OPEN_WALLET_REQUEST_CODE, extra);
     }

     public static void openWalletWithCodeNew(Activity activity, int extra) {
         Log.d("mylog130", "openWalletWithCodeNew=" + activity + " extra=" + extra + " activity=" + activity.getClass());
         WalletCodeActivityNew.startWalletCodeActivityForResult(activity, OPEN_WALLET_REQUEST_CODE, extra);
     }

     public static void openWalletWithCode(Activity activity, Bundle options) {
         WalletCodeActivity.startWalletCodeActivityForResult(activity, OPEN_WALLET_REQUEST_CODE, options);
     }

     public static void openWalletWithCode(Fragment fragment, Bundle options) {
         WalletCodeActivity.startWalletCodeActivityForResult(fragment, OPEN_WALLET_REQUEST_CODE, options);
     }

     public static void openWalletWithCode(Fragment fragment, int extra, String stringExtra) {
         WalletCodeActivity.startWalletCodeActivityForResult(fragment, OPEN_WALLET_REQUEST_CODE, extra, stringExtra);
     }

     public static void openWalletWithCode(Activity activity, int extra, Serializable serializable) {
         WalletCodeActivity.startWalletCodeActivityForResult(activity, OPEN_WALLET_REQUEST_CODE, extra, serializable);
     }*/

     public interface WalletListener {
         void onWalletOpenSucces(Wallet wallet);

         void onWalletOpenError(int errorCode);
     }


/*     public static boolean onActivityResultOpenWalletNew(int requestCode, int resultCode, @Nullable Intent data) {
         if (requestCode == OPEN_WALLET_REQUEST_CODE) {
             if (resultCode == RESULT_OK) {
                 return true;
             }
         }
         return false;
     }*/

/*
     public static void onActivityResultOpenWallet(int requestCode, int resultCode, @Nullable Intent data, boolean openWallet, WalletListener walletListener) {

         if (requestCode == OPEN_WALLET_REQUEST_CODE) {
             if (resultCode == RESULT_OK) {
                 if (data != null) {
                     String code = data.getStringExtra("code");
                     if (code != null) {
                         Handler handler = new Handler();
                         if (openWallet) {
                             new Thread(new Runnable() {
                                 @Override
                                 public void run() {
                                     try {
                                         Wallet wallet = IndyWallet.openWallet(code);
                                         handler.post(new Runnable() {
                                             @Override
                                             public void run() {
                                                 if (wallet != null) {
                                                     myWallet = wallet;
                                                 }
                                                 if (walletListener != null) {
                                                     if (wallet != null) {
                                                         walletListener.onWalletOpenSucces(wallet);
                                                     } else {
                                                         walletListener.onWalletOpenError(101);
                                                     }
                                                 }
                                             }
                                         });

                                     } catch (Exception e) {
                                         handler.post(new Runnable() {
                                             @Override
                                             public void run() {
                                                 if (walletListener != null) {
                                                     walletListener.onWalletOpenError(102);
                                                 }
                                             }
                                         });
                                         e.printStackTrace();
                                     }
                                 }
                             }).start();
                         } else {
                             if (walletListener != null) {
                                 walletListener.onWalletOpenSucces(null);

                             }
                         }

                     } else {
                         if (walletListener != null) {
                             walletListener.onWalletOpenError(103);
                         }
                     }
                 } else {
                     if (walletListener != null) {
                         walletListener.onWalletOpenError(104);
                     }
                 }
             } else {
                 if (walletListener != null) {
                     walletListener.onWalletOpenError(105);
                 }
             }
         }

     }

*/

 }
