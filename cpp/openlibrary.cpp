#include<jni.h>
#include<string>
#include<curl/curl.h>

static size_t writeCallback(void *ptr, size_t size, size_t nmemb, void* data) {
  ((std::string*)data)->((char*)ptr, size * nmemb);
  return size * nmemb;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_myapp_OpenLibrary_queryBook(JNIEnv* env, jobject, jstring query) {

  // Convert Java string to cpp string
  const char* queryStr = env->GetStringUTFChars(query, 0);
  std::string searchQuery(queryStr);
  env->ReleaseStringUTFChars(query, queryStr);

  // Build the search url for openlibrary
  std::string response;
  CURL* curl = curl_easy_init();
  if (curl) {
    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
    curl_easy_perform(curl);
    curl_easy_cleanup(curl);
  }

  return env->NewStringUTF(response.c_str());
}
