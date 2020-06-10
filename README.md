MedTagger as a RESTful Service
==
Provides access to [MedTagger](https://www.github.com/OHNLP/MedTagger) NLP functionality 
as a REST service as a [UIMA-Stream-Server Plugin](https://www.github.com/OHNLP/UIMA-Stream-Server)

**Installation**

1. Install the REST server module of [UIMA-Stream-Server](https://github.com/OHNLP/UIMA-Stream-Server/tree/master/UIMA-Server-REST)
2. Download the distributable ZIP file of the [latest MedTagger release](https://github.com/OHNLP/MedTagger/releases/latest) (file named MedTagger.zip)
3. Extract contents as-is to the UIMA-Stream-Server's working directory (medtaggerieresources folder should be at same level as the libs folder)
4. Move MedTagger.jar into the /libs folder
5. Download the [latest MedTaggerRESTPlugin release](https://github.com/OHNLP/MedTaggerRESTPlugin/releases) (file named MedTaggerRESTPlugin.zip)
6. Extract contents to UIMA-Stream-Server's working directory (medtagger_rest_config.json should be at same level as the libs folder)
7. Edit `medtagger_rest_config.json` file as appropriate

**To Use**

Direct a POST call to `http(s)://www.yourserver.com:portnumber/` with a json body as below:

```json
{
  "streamName": "RULESET_ID_AS_DEFINED_IN_medtagger_rest_config.json",
  "metadata": "",
  "document": "TEXT_TO_PROCESS",
  "serializers": ["medtagger"]
}
```

**For Developers/Local Development**
To access github package repositories for dependency resolution, you will need to
generate an appropriate [github token](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
with the `read:packages` permissions, and edit settings.xml appropriately by replacing 
`${env.SECRET_ACTOR}` with your github username and `${env.SECRET_TOKEN}` with the generated token.

Alternatively, fork the repository, go to settings, and add `SECRET_ACTOR` and `SECRET_TOKEN`


