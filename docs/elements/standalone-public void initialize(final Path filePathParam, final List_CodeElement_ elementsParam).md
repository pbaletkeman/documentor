# 📁 Standalone Elements

These elements are not associated with a specific class.

---

## 📑 Table of Contents

<details open>
<summary><strong>🔸 Methods</strong> (294)</summary>

- [🔧 initialize](#initialize)
- [🔧 initialize](#initialize)
- [🔧 visit](#visit)
- [🔧 visit](#visit)
- [🔧 visit](#visit)
- [🔧 visit](#visit)
- [🔧 extractSignature](#extractsignature)
- [🔧 extractSignature](#extractsignature)
- [🔧 extractParameters](#extractparameters)
- [🔧 analyzeProject](#analyzeproject)

- [🔧 analyzeProject](#analyzeproject)
- [🔧 discoverAndAnalyzeFiles](#discoverandanalyzefiles)
- [🔧 discoverAndAnalyzeFiles](#discoverandanalyzefiles)
- [🔧 isSupportedFile](#issupportedfile)
- [🔧 shouldAnalyzeFile](#shouldanalyzefile)
- [🔧 analyzeFileSafely](#analyzefilesafely)
- [🔧 analyzeFileSafely](#analyzefilesafely)
- [🔧 analyzeFileByType](#analyzefilebytype)
- [🔧 analyzeFileByType](#analyzefilebytype)
- [🔧 groupElementsByClass](#groupelementsbyclass)

- [🔧 getEligibleClasses](#geteligibleclasses)
- [🔧 getElementsForClass](#getelementsforclass)
- [🔧 isNonPrivate](#isnonprivate)
- [🔧 groupElementsByFile](#groupelementsbyfile)
- [🔧 getClassDiagramGenerator](#getclassdiagramgenerator)
- [🔧 getMermaidClassDiagramGenerator](#getmermaidclassdiagramgenerator)
- [🔧 getPlantUMLClassDiagramGenerator](#getplantumlclassdiagramgenerator)
- [🔧 determineOutputPath](#determineoutputpath)
- [🔧 generateDiagramFileName](#generatediagramfilename)
- [🔧 generateDiagramFileName](#generatediagramfilename)

- [🔧 sanitizeFileName](#sanitizefilename)
- [🔧 createOutputDirectory](#createoutputdirectory)
- [🔧 generateClassDiagram](#generateclassdiagram)
- [🔧 generateClassDiagram](#generateclassdiagram)
- [🔧 addClassToMermaid](#addclasstomermaid)
- [🔧 addRelationshipsToMermaid](#addrelationshipstomermaid)
- [🔧 sanitizeClassName](#sanitizeclassname)
- [🔧 sanitizeSignature](#sanitizesignature)
- [🔧 isNonPrivate](#isnonprivate)
- [🔧 generateClassDiagram](#generateclassdiagram)

- [🔧 generateClassDiagram](#generateclassdiagram)
- [🔧 addClassToPlantUML](#addclasstoplantuml)
- [🔧 determineClassType](#determineclasstype)
- [🔧 addFieldToPlantUML](#addfieldtoplantuml)
- [🔧 addMethodToPlantUML](#addmethodtoplantuml)
- [🔧 mapVisibilityToPlantUML](#mapvisibilitytoplantuml)
- [🔧 addRelationshipsToPlantUML](#addrelationshipstoplantuml)
- [🔧 extractReturnType](#extractreturntype)
- [🔧 extractParameters](#extractparameters)
- [🔧 sanitizeClassName](#sanitizeclassname)

- [🔧 sanitizeType](#sanitizetype)
- [🔧 isNonPrivate](#isnonprivate)
- [🔧 appendHeader](#appendheader)
- [🔧 appendStatistics](#appendstatistics)
- [🔧 appendUsageExamples](#appendusageexamples)
- [🔧 appendApiReference](#appendapireference)
- [🔧 appendTestDocumentationHeader](#appendtestdocumentationheader)
- [🔧 getProjectName](#getprojectname)
- [🔧 generateElementDocumentation](#generateelementdocumentation)
- [🔧 generateGroupedDocumentation](#generategroupeddocumentation)

- [🔧 generateClassDocumentation](#generateclassdocumentation)
- [🔧 getElement](#getelement)
- [🔧 getDocumentation](#getdocumentation)
- [🔧 getExamples](#getexamples)
- [🔧 generateElementDocPair](#generateelementdocpair)
- [🔧 buildClassDocumentContent](#buildclassdocumentcontent)
- [🔧 buildClassHeaderSection](#buildclassheadersection)
- [🔧 buildTableOfContents](#buildtableofcontents)
- [🔧 buildFieldsSection](#buildfieldssection)
- [🔧 buildMethodsSection](#buildmethodssection)

- [🔧 formatContent](#formatcontent)
- [🔧 formatCodeBlock](#formatcodeblock)
- [🔧 sanitizeAnchor](#sanitizeanchor)
- [🔧 groupElementsByClass](#groupelementsbyclass)
- [🔧 getLanguageFromFile](#getlanguagefromfile)
- [🔧 generateElementDocumentation](#generateelementdocumentation)
- [🔧 generateGroupedDocumentation](#generategroupeddocumentation)
- [🔧 generateClassDocumentation](#generateclassdocumentation)
- [🔧 validateThreadLocalConfig](#validatethreadlocalconfig)
- [🔧 generateClassDocumentation](#generateclassdocumentation)

- [🔧 generateClassExamples](#generateclassexamples)
- [🔧 generateFieldsDocumentation](#generatefieldsdocumentation)
- [🔧 generateMethodsDocumentation](#generatemethodsdocumentation)
- [🔧 writeDocumentationToFile](#writedocumentationtofile)
- [🔧 determineFileName](#determinefilename)
- [🔧 getElement](#getelement)
- [🔧 getDocumentation](#getdocumentation)
- [🔧 getExamples](#getexamples)
- [🔧 generateElementDocPair](#generateelementdocpair)
- [🔧 buildClassDocumentContent](#buildclassdocumentcontent)

- [🔧 buildClassHeaderSection](#buildclassheadersection)
- [🔧 buildTableOfContents](#buildtableofcontents)
- [🔧 buildFieldsTableOfContents](#buildfieldstableofcontents)
- [🔧 buildMethodsTableOfContents](#buildmethodstableofcontents)
- [🔧 buildStandaloneElementsHeader](#buildstandaloneelementsheader)
- [🔧 buildFieldsSection](#buildfieldssection)
- [🔧 buildMethodsSection](#buildmethodssection)
- [🔧 buildSingleMethodSection](#buildsinglemethodsection)
- [🔧 buildMethodSignature](#buildmethodsignature)
- [🔧 formatContent](#formatcontent)

- [🔧 formatCodeBlock](#formatcodeblock)
- [🔧 sanitizeAnchor](#sanitizeanchor)
- [🔧 groupElementsByClass](#groupelementsbyclass)
- [🔧 getLanguageFromFile](#getlanguagefromfile)
- [🔧 generateMainDocumentation](#generatemaindocumentation)
- [🔧 appendHeader](#appendheader)
- [🔧 appendStatistics](#appendstatistics)
- [🔧 appendApiReference](#appendapireference)
- [🔧 appendUsageExamples](#appendusageexamples)
- [🔧 generateUnitTestDocumentation](#generateunittestdocumentation)

- [🔧 appendTestDocumentationHeader](#appendtestdocumentationheader)
- [🔧 generateUnitTestDocumentation](#generateunittestdocumentation)
- [🔧 appendTestDocumentationHeader](#appendtestdocumentationheader)
- [🔧 generateDocumentation](#generatedocumentation)
- [🔧 generateDocumentation](#generatedocumentation)
- [🔧 generateDetailedDocumentation](#generatedetaileddocumentation)
- [🔧 generateDocumentation](#generatedocumentation)
- [🔧 setupThreadLocalConfig](#setupthreadlocalconfig)
- [🔧 generateMainDocumentation](#generatemaindocumentation)
- [🔧 generateElementDocumentation](#generateelementdocumentation)

- [🔧 generateUnitTestDocumentation](#generateunittestdocumentation)
- [🔧 generateMermaidDiagrams](#generatemermaiddiagrams)
- [🔧 generatePlantUMLDiagrams](#generateplantumldiagrams)
- [🔧 cleanupThreadLocalResources](#cleanupthreadlocalresources)
- [🔧 writeFile](#writefile)
- [🔧 writeFile](#writefile)
- [🔧 getLastWrittenPath](#getlastwrittenpath)
- [🔧 handleCollision](#handlecollision)
- [🔧 generateSuffixedPath](#generatesuffixedpath)
- [🔧 writeAtomically](#writeatomically)

- [🔧 writeAtomically](#writeatomically)
- [🔧 generateTempPath](#generatetemppath)
- [🔧 analyzeFile](#analyzefile)
- [🔧 analyzeFile](#analyzefile)
- [🔧 callLlmModel](#callllmmodel)
- [🔧 isOllamaModel](#isollamamodel)
- [🔧 isOpenAICompatible](#isopenaicompatible)
- [🔧 getModelEndpoint](#getmodelendpoint)
- [🔧 createDocumentationPrompt](#createdocumentationprompt)
- [🔧 createUsageExamplePrompt](#createusageexampleprompt)

- [🔧 createUnitTestPrompt](#createunittestprompt)
- [🔧 buildRequestBody](#buildrequestbody)
- [🔧 createDocumentationPrompt](#createdocumentationprompt)
- [🔧 createUsageExamplePrompt](#createusageexampleprompt)
- [🔧 createUnitTestPrompt](#createunittestprompt)
- [🔧 createRequest](#createrequest)
- [🔧 createOllamaRequest](#createollamarequest)
- [🔧 createOpenAIRequest](#createopenairequest)
- [🔧 createGenericRequest](#creategenericrequest)
- [🔧 extractResponseContent](#extractresponsecontent)

- [🔧 getModelEndpoint](#getmodelendpoint)
- [🔧 parseResponse](#parseresponse)
- [🔧 parseOllamaResponse](#parseollamaresponse)
- [🔧 parseOpenAIResponse](#parseopenairesponse)
- [🔧 parseGenericResponse](#parsegenericresponse)
- [🔧 getProviderName](#getprovidername)
- [🔧 complete](#complete)
- [🔧 complete](#complete)
- [🔧 chat](#chat)
- [🔧 chat](#chat)

- [🔧 getDefaultModel](#getdefaultmodel)
- [🔧 setDefaultModel](#setdefaultmodel)
- [🔧 isAvailable](#isavailable)
- [🔧 generateMockCompletion](#generatemockcompletion)
- [🔧 getProviderName](#getprovidername)
- [🔧 complete](#complete)
- [🔧 complete](#complete)
- [🔧 chat](#chat)
- [🔧 chat](#chat)
- [🔧 getDefaultModel](#getdefaultmodel)

- [🔧 setDefaultModel](#setdefaultmodel)
- [🔧 isAvailable](#isavailable)
- [🔧 getId](#getid)
- [🔧 fromId](#fromid)
- [🔧 createProvider](#createprovider)
- [🔧 createProvider](#createprovider)
- [🔧 createProvider](#createprovider)
- [🔧 createProvider](#createprovider)
- [🔧 getProvider](#getprovider)
- [🔧 getProvider](#getprovider)

- [🔧 getProvider](#getprovider)
- [🔧 getProvider](#getprovider)
- [🔧 clearCache](#clearcache)
- [🔧 removeFromCache](#removefromcache)
- [🔧 removeFromCache](#removefromcache)
- [🔧 getCacheSize](#getcachesize)
- [🔧 getProviderName](#getprovidername)
- [🔧 complete](#complete)
- [🔧 complete](#complete)
- [🔧 chat](#chat)

- [🔧 chat](#chat)
- [🔧 getDefaultModel](#getdefaultmodel)
- [🔧 setDefaultModel](#setdefaultmodel)
- [🔧 isAvailable](#isavailable)
- [🔧 generateMockCompletion](#generatemockcompletion)
- [🔧 getProviderName](#getprovidername)
- [🔧 complete](#complete)
- [🔧 complete](#complete)
- [🔧 chat](#chat)
- [🔧 chat](#chat)

- [🔧 getDefaultModel](#getdefaultmodel)
- [🔧 setDefaultModel](#setdefaultmodel)
- [🔧 isAvailable](#isavailable)
- [🔧 generateMockCompletion](#generatemockcompletion)
- [🔧 getThreadLocalConfig](#getthreadlocalconfig)
- [🔧 setThreadLocalConfig](#setthreadlocalconfig)
- [🔧 clearThreadLocalConfig](#clearthreadlocalconfig)
- [🔧 generateDocumentation](#generatedocumentation)
- [🔧 generateUsageExamples](#generateusageexamples)
- [🔧 generateUnitTests](#generateunittests)

- [🔧 getWorkerThreadCount](#getworkerthreadcount)
- [🔧 generateWithModel](#generatewithmodel)
- [🔧 createPrompt](#createprompt)
- [🔧 getThreadLocalConfig](#getthreadlocalconfig)
- [🔧 setThreadLocalConfig](#setthreadlocalconfig)
- [🔧 clearThreadLocalConfig](#clearthreadlocalconfig)
- [🔧 getExecutor](#getexecutor)
- [🔧 generateDocumentation](#generatedocumentation)
- [🔧 generateUsageExamples](#generateusageexamples)
- [🔧 generateUnitTests](#generateunittests)

- [🔧 getWorkerThreadCount](#getworkerthreadcount)
- [🔧 generateWithModel](#generatewithmodel)
- [🔧 createPrompt](#createprompt)
- [🔧 setLlmServiceThreadLocalConfig](#setllmservicethreadlocalconfig)
- [🔧 isThreadLocalConfigAvailable](#isthreadlocalconfigavailable)
- [🔧 setLlmServiceThreadLocalConfig](#setllmservicethreadlocalconfig)
- [🔧 isThreadLocalConfigAvailable](#isthreadlocalconfigavailable)
- [🔧 cleanupThreadLocalConfig](#cleanupthreadlocalconfig)
- [🔧 executeWithConfig](#executewithconfig)
- [🔧 generateClassDiagrams](#generateclassdiagrams)

- [🔧 generateClassDiagrams](#generateclassdiagrams)
- [🔧 generateDiagrams](#generatediagrams)
- [🔧 processSingleClassDiagram](#processsingleclassdiagram)
- [🔧 generateClassDiagrams](#generateclassdiagrams)
- [🔧 generateClassDiagrams](#generateclassdiagrams)
- [🔧 generateDiagrams](#generatediagrams)
- [🔧 processSingleClassDiagram](#processsingleclassdiagram)
- [🔧 getPythonAstScript](#getpythonastscript)
- [🔧 writeTempScript](#writetempscript)
- [🔧 createProcessBuilder](#createprocessbuilder)

- [🔧 parseASTOutputLine](#parseastoutputline)
- [🔧 analyzeWithAST](#analyzewithast)
- [🔧 processOutput](#processoutput)
- [🔧 extractDocstring](#extractdocstring)
- [🔧 extractParameters](#extractparameters)
- [🔧 findClassMatches](#findclassmatches)
- [🔧 findFunctionMatches](#findfunctionmatches)
- [🔧 findVariableMatches](#findvariablematches)
- [🔧 findDocstring](#finddocstring)
- [🔧 extractParameters](#extractparameters)

- [🔧 analyzeWithRegex](#analyzewithregex)
- [🔧 processClassElements](#processclasselements)
- [🔧 processFunctionElements](#processfunctionelements)
- [🔧 processVariableElements](#processvariableelements)
- [🔧 getLineNumber](#getlinenumber)
- [🔧 shouldInclude](#shouldinclude)
- [🔧 analyzeFile](#analyzefile)
- [🔧 analyzeFile](#analyzefile)
- [🔧 calculateSuccessRate](#calculatesuccessrate)
- [🔧 formatSuccessRate](#formatsuccessrate)

- [🔧 meetsSuccessThreshold](#meetssuccessthreshold)
- [🔧 calculateErrorRate](#calculateerrorrate)
- [🔧 isServiceHealthy](#isservicehealthy)
- [🔧 calculateAvailability](#calculateavailability)
- [🔧 formatMetricsSummary](#formatmetricssummary)
- [🔧 measureTime](#measuretime)
- [🔧 isWithinTimeLimit](#iswithintimelimit)
- [🔧 calculateThroughput](#calculatethroughput)
- [🔧 batchElements](#batchelements)
- [🔧 estimateProcessingTime](#estimateprocessingtime)

- [🔧 isCompletedWithinTimeout](#iscompletedwithintimeout)
- [🔧 formatDuration](#formatduration)
- [🔧 calculateOptimalBatchSize](#calculateoptimalbatchsize)
- [🔧 validatePerformanceMetrics](#validateperformancemetrics)
- [🔧 filterByType](#filterbytype)
- [🔧 groupByType](#groupbytype)
- [🔧 isValidTimeout](#isvalidtimeout)
- [🔧 calculateAdaptiveTimeout](#calculateadaptivetimeout)
- [🔧 sanitizeFilePath](#sanitizefilepath)
- [🔧 isSupportedDocType](#issupporteddoctype)

- [🔧 createDisplayName](#createdisplayname)
- [🔧 validateOperationParameters](#validateoperationparameters)
- [🔧 isRequiredParameter](#isrequiredparameter)
- [🔧 formatErrorMessage](#formaterrormessage)
- [🔧 getRetryDelay](#getretrydelay)
- [🔧 validateCodeElement](#validatecodeelement)
- [🔧 hasDuplicateNames](#hasduplicatenames)
- [🔧 countByType](#countbytype)
- [🔧 hasMissingDocumentation](#hasmissingdocumentation)
- [🔧 getUniqueFilePaths](#getuniquefilepaths)

- [🔧 isValidOperation](#isvalidoperation)
- [🔧 calculateCoverage](#calculatecoverage)
- [🔧 formatCoverage](#formatcoverage)
- [🔧 meetsCoverageThreshold](#meetscoveragethreshold)

</details>

---

## 🔸 Methods

<div class="element-box">

### 🔧 initialize

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void initialize(final Path filePathParam, final List<CodeElement> elementsParam)
```

</div>

---

<div class="element-box">

### 🔧 initialize

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void initialize(final Path filePathParam, final List<CodeElement> elementsParam, final Boolean includePrivateMembersOverrideParam)
```

</div>

---

<div class="element-box">

### 🔧 visit

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final void visit(final ClassOrInterfaceDeclaration declaration, final Void arg)
```

</div>

---

<div class="element-box">

### 🔧 visit

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final void visit(final EnumDeclaration declaration, final Void arg)
```

</div>

---

<div class="element-box">

### 🔧 visit

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final void visit(final MethodDeclaration declaration, final Void arg)
```

</div>

---

<div class="element-box">

### 🔧 visit

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final void visit(final FieldDeclaration declaration, final Void arg)
```

</div>

---

<div class="element-box">

### 🔧 extractSignature

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String extractSignature(final ClassOrInterfaceDeclaration declaration)
```

</div>

---

<div class="element-box">

### 🔧 extractSignature

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String extractSignature(final EnumDeclaration declaration)
```

</div>

---

<div class="element-box">

### 🔧 extractParameters

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private List<String> extractParameters(final MethodDeclaration declaration)
```

</div>

---

<div class="element-box">

### 🔧 analyzeProject

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<ProjectAnalysis> analyzeProject(final Path projectPath)
```

</div>

---

<div class="element-box">

### 🔧 analyzeProject

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<ProjectAnalysis> analyzeProject(final Path projectPath, final Boolean includePrivateMembersOverride)
```

</div>

---

<div class="element-box">

### 🔧 discoverAndAnalyzeFiles

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private List<CodeElement> discoverAndAnalyzeFiles(final Path projectPath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 discoverAndAnalyzeFiles

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private List<CodeElement> discoverAndAnalyzeFiles(final Path projectPath, final Boolean includePrivateMembersOverride) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 isSupportedFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean isSupportedFile(final Path file)
```

</div>

---

<div class="element-box">

### 🔧 shouldAnalyzeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean shouldAnalyzeFile(final Path file)
```

</div>

---

<div class="element-box">

### 🔧 analyzeFileSafely

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Stream<CodeElement> analyzeFileSafely(final Path file)
```

</div>

---

<div class="element-box">

### 🔧 analyzeFileSafely

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Stream<CodeElement> analyzeFileSafely(final Path file, final Boolean includePrivateMembersOverride)
```

</div>

---

<div class="element-box">

### 🔧 analyzeFileByType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Stream<CodeElement> analyzeFileByType(final Path file) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 analyzeFileByType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Stream<CodeElement> analyzeFileByType(final Path file, final Boolean includePrivateMembersOverride) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 groupElementsByClass

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Map<CodeElement, List<CodeElement>> groupElementsByClass(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 getEligibleClasses

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> getEligibleClasses(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 getElementsForClass

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> getElementsForClass(final List<CodeElement> allElements, final CodeElement classElement)
```

</div>

---

<div class="element-box">

### 🔧 isNonPrivate

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isNonPrivate(final CodeElement element)
```

</div>

---

<div class="element-box">

### 🔧 groupElementsByFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Map<String, List<CodeElement>> groupElementsByFile(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 getClassDiagramGenerator

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public MermaidClassDiagramGenerator getClassDiagramGenerator()
```

</div>

---

<div class="element-box">

### 🔧 getMermaidClassDiagramGenerator

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public MermaidClassDiagramGenerator getMermaidClassDiagramGenerator()
```

</div>

---

<div class="element-box">

### 🔧 getPlantUMLClassDiagramGenerator

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public PlantUMLClassDiagramGenerator getPlantUMLClassDiagramGenerator()
```

</div>

---

<div class="element-box">

### 🔧 determineOutputPath

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String determineOutputPath(final String sourceFilePath, final String customOutputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateDiagramFileName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String generateDiagramFileName(final String className)
```

</div>

---

<div class="element-box">

### 🔧 generateDiagramFileName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String generateDiagramFileName(final String className, final DiagramNamingOptions namingOptions, final String defaultExtension)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeFileName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeFileName(final String fileName)
```

</div>

---

<div class="element-box">

### 🔧 createOutputDirectory

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Path createOutputDirectory(final String outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagram

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String generateClassDiagram(final CodeElement classElement, final List<CodeElement> allElements, final Path outputPath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagram

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String generateClassDiagram(final CodeElement classElement, final List<CodeElement> allElements, final Path outputPath, final DiagramNamingOptions namingOptions) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 addClassToMermaid

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void addClassToMermaid(final StringBuilder diagram, final CodeElement classElement, final List<CodeElement> allElements)
```

</div>

---

<div class="element-box">

### 🔧 addRelationshipsToMermaid

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void addRelationshipsToMermaid(final StringBuilder diagram, final CodeElement classElement, final List<CodeElement> allElements)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeClassName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeClassName(final String className)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeSignature

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeSignature(final String signature)
```

</div>

---

<div class="element-box">

### 🔧 isNonPrivate

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean isNonPrivate(final CodeElement element)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagram

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String generateClassDiagram(final CodeElement classElement, final List<CodeElement> allElements, final Path outputPath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagram

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String generateClassDiagram(final CodeElement classElement, final List<CodeElement> allElements, final Path outputPath, final DiagramNamingOptions namingOptions) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 addClassToPlantUML

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void addClassToPlantUML(final StringBuilder diagram, final CodeElement classElement, final List<CodeElement> allElements)
```

</div>

---

<div class="element-box">

### 🔧 determineClassType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String determineClassType(final CodeElement classElement)
```

</div>

---

<div class="element-box">

### 🔧 addFieldToPlantUML

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void addFieldToPlantUML(final StringBuilder diagram, final CodeElement field)
```

</div>

---

<div class="element-box">

### 🔧 addMethodToPlantUML

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void addMethodToPlantUML(final StringBuilder diagram, final CodeElement method)
```

</div>

---

<div class="element-box">

### 🔧 mapVisibilityToPlantUML

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String mapVisibilityToPlantUML(final CodeElement element)
```

</div>

---

<div class="element-box">

### 🔧 addRelationshipsToPlantUML

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void addRelationshipsToPlantUML(final StringBuilder diagram, final CodeElement classElement, final List<CodeElement> allElements)
```

</div>

---

<div class="element-box">

### 🔧 extractReturnType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String extractReturnType(final String signature)
```

</div>

---

<div class="element-box">

### 🔧 extractParameters

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String extractParameters(final String signature)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeClassName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeClassName(final String name)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeType(final String type)
```

</div>

---

<div class="element-box">

### 🔧 isNonPrivate

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean isNonPrivate(final CodeElement element)
```

</div>

---

<div class="element-box">

### 🔧 appendHeader

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void appendHeader(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendStatistics

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void appendStatistics(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendUsageExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void appendUsageExamples(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendApiReference

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void appendApiReference(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendTestDocumentationHeader

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void appendTestDocumentationHeader(final StringBuilder doc)
```

</div>

---

<div class="element-box">

### 🔧 getProjectName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String getProjectName(final String projectPath)
```

</div>

---

<div class="element-box">

### 🔧 generateElementDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<Void> generateElementDocumentation(final CodeElement element, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateGroupedDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<Void> generateGroupedDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<Void> generateClassDocumentation(final CodeElement classElement, final List<CodeElement> classElements, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 getElement

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CodeElement getElement()
```

</div>

---

<div class="element-box">

### 🔧 getDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getDocumentation()
```

</div>

---

<div class="element-box">

### 🔧 getExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getExamples()
```

</div>

---

<div class="element-box">

### 🔧 generateElementDocPair

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<ElementDocPair> generateElementDocPair(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 buildClassDocumentContent

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String buildClassDocumentContent(final CodeElement classElement, final String classDoc, final String classExamples, final List<ElementDocPair> fields, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 buildClassHeaderSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildClassHeaderSection(final StringBuilder content, final CodeElement classElement, final String classDoc, final String classExamples)
```

</div>

---

<div class="element-box">

### 🔧 buildTableOfContents

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildTableOfContents(final StringBuilder content, final List<ElementDocPair> fields, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 buildFieldsSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildFieldsSection(final StringBuilder content, final List<ElementDocPair> fields)
```

</div>

---

<div class="element-box">

### 🔧 buildMethodsSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildMethodsSection(final StringBuilder content, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 formatContent

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String formatContent(final String content)
```

</div>

---

<div class="element-box">

### 🔧 formatCodeBlock

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String formatCodeBlock(final String code)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeAnchor

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeAnchor(final String text)
```

</div>

---

<div class="element-box">

### 🔧 groupElementsByClass

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Map<String, List<CodeElement>> groupElementsByClass(final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 getLanguageFromFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String getLanguageFromFile(final String filePath)
```

</div>

---

<div class="element-box">

### 🔧 generateElementDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<Void> generateElementDocumentation(final CodeElement element, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateGroupedDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<Void> generateGroupedDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<Void> generateClassDocumentation(final CodeElement classElement, final List<CodeElement> classElements, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 validateThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void validateThreadLocalConfig(final CodeElement classElement)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<String> generateClassDocumentation(final CodeElement classElement)
```

</div>

---

<div class="element-box">

### 🔧 generateClassExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<String> generateClassExamples(final CodeElement classElement)
```

</div>

---

<div class="element-box">

### 🔧 generateFieldsDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<List<ElementDocPair>> generateFieldsDocumentation(final List<CodeElement> fields)
```

</div>

---

<div class="element-box">

### 🔧 generateMethodsDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<List<ElementDocPair>> generateMethodsDocumentation(final List<CodeElement> methods)
```

</div>

---

<div class="element-box">

### 🔧 writeDocumentationToFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

<details>
<summary>View Method Signature</summary>

```java
private Void writeDocumentationToFile(final CodeElement classElement, final List<CodeElement> classElements, final Path outputPath, final CompletableFuture<String> classFuture, final CompletableFuture<String> classExamplesFuture, final CompletableFuture<List<ElementDocPair>> allFieldsFuture, final CompletableFuture<List<ElementDocPair>> allMethodsFuture)
```

</details>

</div>

---

<div class="element-box">

### 🔧 determineFileName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String determineFileName(final CodeElement classElement, final List<CodeElement> classElements)
```

</div>

---

<div class="element-box">

### 🔧 getElement

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CodeElement getElement()
```

</div>

---

<div class="element-box">

### 🔧 getDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getDocumentation()
```

</div>

---

<div class="element-box">

### 🔧 getExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getExamples()
```

</div>

---

<div class="element-box">

### 🔧 generateElementDocPair

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<ElementDocPair> generateElementDocPair(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 buildClassDocumentContent

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String buildClassDocumentContent(final CodeElement classElement, final String classDoc, final String classExamples, final List<ElementDocPair> fields, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 buildClassHeaderSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

<details>
<summary>View Method Signature</summary>

```java
private void buildClassHeaderSection(final StringBuilder content, final CodeElement classElement, final String classDoc, final String classExamples, final List<ElementDocPair> fields, final List<ElementDocPair> methods)
```

</details>

</div>

---

<div class="element-box">

### 🔧 buildTableOfContents

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildTableOfContents(final StringBuilder content, final List<ElementDocPair> fields, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 buildFieldsTableOfContents

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildFieldsTableOfContents(final StringBuilder content, final List<ElementDocPair> fields)
```

</div>

---

<div class="element-box">

### 🔧 buildMethodsTableOfContents

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildMethodsTableOfContents(final StringBuilder content, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 buildStandaloneElementsHeader

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildStandaloneElementsHeader(final StringBuilder content)
```

</div>

---

<div class="element-box">

### 🔧 buildFieldsSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildFieldsSection(final StringBuilder content, final List<ElementDocPair> fields)
```

</div>

---

<div class="element-box">

### 🔧 buildMethodsSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildMethodsSection(final StringBuilder content, final List<ElementDocPair> methods)
```

</div>

---

<div class="element-box">

### 🔧 buildSingleMethodSection

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildSingleMethodSection(final StringBuilder content, final ElementDocPair method)
```

</div>

---

<div class="element-box">

### 🔧 buildMethodSignature

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void buildMethodSignature(final StringBuilder content, final CodeElement methodElem)
```

</div>

---

<div class="element-box">

### 🔧 formatContent

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String formatContent(final String content)
```

</div>

---

<div class="element-box">

### 🔧 formatCodeBlock

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String formatCodeBlock(final String code)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeAnchor

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String sanitizeAnchor(final String text)
```

</div>

---

<div class="element-box">

### 🔧 groupElementsByClass

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Map<String, List<CodeElement>> groupElementsByClass(final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 getLanguageFromFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String getLanguageFromFile(final String filePath)
```

</div>

---

<div class="element-box">

### 🔧 generateMainDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<String> generateMainDocumentation(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendHeader

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void appendHeader(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendStatistics

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void appendStatistics(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendApiReference

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void appendApiReference(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 appendUsageExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void appendUsageExamples(final StringBuilder doc, final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 generateUnitTestDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<Void> generateUnitTestDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 appendTestDocumentationHeader

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void appendTestDocumentationHeader(final StringBuilder doc)
```

</div>

---

<div class="element-box">

### 🔧 generateUnitTestDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<Void> generateUnitTestDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 appendTestDocumentationHeader

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void appendTestDocumentationHeader(final StringBuilder doc)
```

</div>

---

<div class="element-box">

### 🔧 generateDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<String> generateDocumentation(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 generateDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<String> generateDocumentation(final ProjectAnalysis analysis, final boolean skipDiagrams)
```

</div>

---

<div class="element-box">

### 🔧 generateDetailedDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private CompletableFuture<Void> generateDetailedDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<String> generateDocumentation(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 setupThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void setupThreadLocalConfig(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 generateMainDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void generateMainDocumentation(final ProjectAnalysis analysis, final Path outputPath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 generateElementDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void generateElementDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateUnitTestDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void generateUnitTestDocumentation(final ProjectAnalysis analysis, final Path outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateMermaidDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void generateMermaidDiagrams(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 generatePlantUMLDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void generatePlantUMLDiagrams(final ProjectAnalysis analysis)
```

</div>

---

<div class="element-box">

### 🔧 cleanupThreadLocalResources

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void cleanupThreadLocalResources()
```

</div>

---

<div class="element-box">

### 🔧 writeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean writeFile(final Path targetPath, final String content) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 writeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean writeFile(final Path targetPath, final byte[] content) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 getLastWrittenPath

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Path getLastWrittenPath()
```

</div>

---

<div class="element-box">

### 🔧 handleCollision

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Path handleCollision(final Path targetPath)
```

</div>

---

<div class="element-box">

### 🔧 generateSuffixedPath

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Path generateSuffixedPath(final Path originalPath)
```

</div>

---

<div class="element-box">

### 🔧 writeAtomically

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean writeAtomically(final Path targetPath, final String content) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 writeAtomically

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean writeAtomically(final Path targetPath, final byte[] content) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 generateTempPath

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Path generateTempPath(final Path targetPath)
```

</div>

---

<div class="element-box">

### 🔧 analyzeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> analyzeFile(final Path filePath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 analyzeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> analyzeFile(final Path filePath, final Boolean includePrivateMembersOverride) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 callLlmModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String callLlmModel(final LlmModelConfig model, final String endpoint, final Map<String, Object> requestBody)
```

</div>

---

<div class="element-box">

### 🔧 isOllamaModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isOllamaModel(final LlmModelConfig model)
```

</div>

---

<div class="element-box">

### 🔧 isOpenAICompatible

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isOpenAICompatible(final LlmModelConfig model)
```

</div>

---

<div class="element-box">

### 🔧 getModelEndpoint

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getModelEndpoint(final LlmModelConfig model)
```

</div>

---

<div class="element-box">

### 🔧 createDocumentationPrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String createDocumentationPrompt(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 createUsageExamplePrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String createUsageExamplePrompt(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 createUnitTestPrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String createUnitTestPrompt(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 buildRequestBody

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Map<String, Object> buildRequestBody(final LlmModelConfig model, final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 createDocumentationPrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String createDocumentationPrompt(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 createUsageExamplePrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String createUsageExamplePrompt(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 createUnitTestPrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String createUnitTestPrompt(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 createRequest

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Map<String, Object> createRequest(final LlmModelConfig model, final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 createOllamaRequest

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Map<String, Object> createOllamaRequest(final LlmModelConfig model, final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 createOpenAIRequest

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Map<String, Object> createOpenAIRequest(final LlmModelConfig model, final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 createGenericRequest

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Map<String, Object> createGenericRequest(final LlmModelConfig model, final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 extractResponseContent

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String extractResponseContent(final String response, final LlmModelConfig model)
```

</div>

---

<div class="element-box">

### 🔧 getModelEndpoint

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getModelEndpoint(final LlmModelConfig model)
```

</div>

---

<div class="element-box">

### 🔧 parseResponse

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String parseResponse(final String response, final LlmModelConfig model)
```

</div>

---

<div class="element-box">

### 🔧 parseOllamaResponse

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String parseOllamaResponse(final String response)
```

</div>

---

<div class="element-box">

### 🔧 parseOpenAIResponse

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String parseOpenAIResponse(final String response)
```

</div>

---

<div class="element-box">

### 🔧 parseGenericResponse

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String parseGenericResponse(final String response)
```

</div>

---

<div class="element-box">

### 🔧 getProviderName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getProviderName()
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String complete(final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String complete(final String prompt, final String model)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String chat(final List<ChatMessage> messages)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String chat(final List<ChatMessage> messages, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getDefaultModel()
```

</div>

---

<div class="element-box">

### 🔧 setDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void setDefaultModel(final String model)
```

</div>

---

<div class="element-box">

### 🔧 isAvailable

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isAvailable()
```

</div>

---

<div class="element-box">

### 🔧 generateMockCompletion

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String generateMockCompletion(final String prompt, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getProviderName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 String getProviderName()
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 String complete(String prompt)
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 String complete(String prompt, String model)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 String chat(List<ChatMessage> messages)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 String chat(List<ChatMessage> messages, String model)
```

</div>

---

<div class="element-box">

### 🔧 getDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 String getDefaultModel()
```

</div>

---

<div class="element-box">

### 🔧 setDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 void setDefaultModel(String model)
```

</div>

---

<div class="element-box">

### 🔧 isAvailable

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
 boolean isAvailable()
```

</div>

---

<div class="element-box">

### 🔧 getId

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getId()
```

</div>

---

<div class="element-box">

### 🔧 fromId

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static ProviderType fromId(final String id)
```

</div>

---

<div class="element-box">

### 🔧 createProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider createProvider(final ProviderType providerType)
```

</div>

---

<div class="element-box">

### 🔧 createProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider createProvider(final ProviderType providerType, final String model)
```

</div>

---

<div class="element-box">

### 🔧 createProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider createProvider(final String providerName)
```

</div>

---

<div class="element-box">

### 🔧 createProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider createProvider(final String providerName, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider getProvider(final ProviderType providerType)
```

</div>

---

<div class="element-box">

### 🔧 getProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider getProvider(final ProviderType providerType, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider getProvider(final String providerName)
```

</div>

---

<div class="element-box">

### 🔧 getProvider

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static MockLlmProvider getProvider(final String providerName, final String model)
```

</div>

---

<div class="element-box">

### 🔧 clearCache

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void clearCache()
```

</div>

---

<div class="element-box">

### 🔧 removeFromCache

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void removeFromCache(final ProviderType providerType)
```

</div>

---

<div class="element-box">

### 🔧 removeFromCache

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void removeFromCache(final ProviderType providerType, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getCacheSize

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static int getCacheSize()
```

</div>

---

<div class="element-box">

### 🔧 getProviderName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getProviderName()
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String complete(final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String complete(final String prompt, final String model)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String chat(final List<ChatMessage> messages)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String chat(final List<ChatMessage> messages, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getDefaultModel()
```

</div>

---

<div class="element-box">

### 🔧 setDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void setDefaultModel(final String model)
```

</div>

---

<div class="element-box">

### 🔧 isAvailable

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isAvailable()
```

</div>

---

<div class="element-box">

### 🔧 generateMockCompletion

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String generateMockCompletion(final String prompt, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getProviderName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getProviderName()
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String complete(final String prompt)
```

</div>

---

<div class="element-box">

### 🔧 complete

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String complete(final String prompt, final String model)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String chat(final List<ChatMessage> messages)
```

</div>

---

<div class="element-box">

### 🔧 chat

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String chat(final List<ChatMessage> messages, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getDefaultModel()
```

</div>

---

<div class="element-box">

### 🔧 setDefaultModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void setDefaultModel(final String model)
```

</div>

---

<div class="element-box">

### 🔧 isAvailable

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isAvailable()
```

</div>

---

<div class="element-box">

### 🔧 generateMockCompletion

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String generateMockCompletion(final String prompt, final String model)
```

</div>

---

<div class="element-box">

### 🔧 getThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static DocumentorConfig getThreadLocalConfig()
```

</div>

---

<div class="element-box">

### 🔧 setThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void setThreadLocalConfig(final DocumentorConfig config)
```

</div>

---

<div class="element-box">

### 🔧 clearThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void clearThreadLocalConfig()
```

</div>

---

<div class="element-box">

### 🔧 generateDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final CompletableFuture<String> generateDocumentation(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 generateUsageExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final CompletableFuture<String> generateUsageExamples(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 generateUnitTests

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final CompletableFuture<String> generateUnitTests(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 getWorkerThreadCount

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private int getWorkerThreadCount()
```

</div>

---

<div class="element-box">

### 🔧 generateWithModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String generateWithModel(final CodeElement codeElement, final LlmModelConfig model, final String type)
```

</div>

---

<div class="element-box">

### 🔧 createPrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String createPrompt(final CodeElement codeElement, final String type)
```

</div>

---

<div class="element-box">

### 🔧 getThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static DocumentorConfig getThreadLocalConfig()
```

</div>

---

<div class="element-box">

### 🔧 setThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void setThreadLocalConfig(final DocumentorConfig config)
```

</div>

---

<div class="element-box">

### 🔧 clearThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static void clearThreadLocalConfig()
```

</div>

---

<div class="element-box">

### 🔧 getExecutor

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private Executor getExecutor()
```

</div>

---

<div class="element-box">

### 🔧 generateDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final CompletableFuture<String> generateDocumentation(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 generateUsageExamples

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final CompletableFuture<String> generateUsageExamples(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 generateUnitTests

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public final CompletableFuture<String> generateUnitTests(final CodeElement codeElement)
```

</div>

---

<div class="element-box">

### 🔧 getWorkerThreadCount

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private int getWorkerThreadCount()
```

</div>

---

<div class="element-box">

### 🔧 generateWithModel

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String generateWithModel(final CodeElement codeElement, final LlmModelConfig model, final String type)
```

</div>

---

<div class="element-box">

### 🔧 createPrompt

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private String createPrompt(final CodeElement codeElement, final String type)
```

</div>

---

<div class="element-box">

### 🔧 setLlmServiceThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void setLlmServiceThreadLocalConfig(final DocumentorConfig config)
```

</div>

---

<div class="element-box">

### 🔧 isThreadLocalConfigAvailable

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isThreadLocalConfigAvailable()
```

</div>

---

<div class="element-box">

### 🔧 setLlmServiceThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void setLlmServiceThreadLocalConfig(final DocumentorConfig config)
```

</div>

---

<div class="element-box">

### 🔧 isThreadLocalConfigAvailable

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public boolean isThreadLocalConfigAvailable()
```

</div>

---

<div class="element-box">

### 🔧 cleanupThreadLocalConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void cleanupThreadLocalConfig()
```

</div>

---

<div class="element-box">

### 🔧 executeWithConfig

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public void executeWithConfig(final DocumentorConfig config, final Runnable runnable)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<List<String>> generateClassDiagrams(final ProjectAnalysis analysis, final String outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<List<String>> generateClassDiagrams(final ProjectAnalysis analysis, final String outputPath, final DiagramNamingOptions namingOptions)
```

</div>

---

<div class="element-box">

### 🔧 generateDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private List<String> generateDiagrams(final ProjectAnalysis analysis, final String outputPath, final DiagramNamingOptions namingOptions)
```

</div>

---

<div class="element-box">

### 🔧 processSingleClassDiagram

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

<details>
<summary>View Method Signature</summary>

```java
private String processSingleClassDiagram(final CodeElement classElement, final Map<CodeElement, List<CodeElement>> elementsByClass, final String outputPath, final DiagramNamingOptions namingOptions) throws Exception
```

</details>

</div>

---

<div class="element-box">

### 🔧 generateClassDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<List<String>> generateClassDiagrams(final ProjectAnalysis analysis, final String outputPath)
```

</div>

---

<div class="element-box">

### 🔧 generateClassDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CompletableFuture<List<String>> generateClassDiagrams(final ProjectAnalysis analysis, final String outputPath, final DiagramNamingOptions namingOptions)
```

</div>

---

<div class="element-box">

### 🔧 generateDiagrams

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private List<String> generateDiagrams(final ProjectAnalysis analysis, final String outputPath, final DiagramNamingOptions namingOptions)
```

</div>

---

<div class="element-box">

### 🔧 processSingleClassDiagram

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

<details>
<summary>View Method Signature</summary>

```java
private String processSingleClassDiagram(final CodeElement classElement, final Map<CodeElement, List<CodeElement>> elementsByClass, final String outputPath, final DiagramNamingOptions namingOptions) throws Exception
```

</details>

</div>

---

<div class="element-box">

### 🔧 getPythonAstScript

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String getPythonAstScript()
```

</div>

---

<div class="element-box">

### 🔧 writeTempScript

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Path writeTempScript() throws IOException
```

</div>

---

<div class="element-box">

### 🔧 createProcessBuilder

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public ProcessBuilder createProcessBuilder(final Path scriptPath, final Path filePath)
```

</div>

---

<div class="element-box">

### 🔧 parseASTOutputLine

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public CodeElement parseASTOutputLine(final String line, final Path filePath)
```

</div>

---

<div class="element-box">

### 🔧 analyzeWithAST

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> analyzeWithAST(final Path filePath) throws IOException, InterruptedException
```

</div>

---

<div class="element-box">

### 🔧 processOutput

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private List<CodeElement> processOutput(final Process process, final Path filePath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 extractDocstring

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String extractDocstring(final List<String> lines, final int startIndex)
```

</div>

---

<div class="element-box">

### 🔧 extractParameters

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<String> extractParameters(final String functionLine)
```

</div>

---

<div class="element-box">

### 🔧 findClassMatches

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Matcher findClassMatches(final String content)
```

</div>

---

<div class="element-box">

### 🔧 findFunctionMatches

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Matcher findFunctionMatches(final String content)
```

</div>

---

<div class="element-box">

### 🔧 findVariableMatches

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public Matcher findVariableMatches(final String content)
```

</div>

---

<div class="element-box">

### 🔧 findDocstring

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String findDocstring(final String content)
```

</div>

---

<div class="element-box">

### 🔧 extractParameters

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public String[] extractParameters(final String paramString)
```

</div>

---

<div class="element-box">

### 🔧 analyzeWithRegex

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> analyzeWithRegex(final Path filePath, final List<String> lines)
```

</div>

---

<div class="element-box">

### 🔧 processClassElements

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void processClassElements(final Path filePath, final List<String> lines, final String content, final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 processFunctionElements

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void processFunctionElements(final Path filePath, final List<String> lines, final String content, final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 processVariableElements

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private void processVariableElements(final Path filePath, final String content, final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 getLineNumber

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private int getLineNumber(final String content, final int position)
```

</div>

---

<div class="element-box">

### 🔧 shouldInclude

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private boolean shouldInclude(final String name)
```

</div>

---

<div class="element-box">

### 🔧 analyzeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> analyzeFile(final Path filePath) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 analyzeFile

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public List<CodeElement> analyzeFile(final Path filePath, final Boolean includePrivateMembersOverride) throws IOException
```

</div>

---

<div class="element-box">

### 🔧 calculateSuccessRate

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static double calculateSuccessRate(final int successful, final int total)
```

</div>

---

<div class="element-box">

### 🔧 formatSuccessRate

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String formatSuccessRate(final double successRate)
```

</div>

---

<div class="element-box">

### 🔧 meetsSuccessThreshold

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean meetsSuccessThreshold(final double successRate, final double threshold)
```

</div>

---

<div class="element-box">

### 🔧 calculateErrorRate

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static double calculateErrorRate(final int errors, final int total)
```

</div>

---

<div class="element-box">

### 🔧 isServiceHealthy

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean isServiceHealthy(final double successRate, final double errorRate, final double minSuccess, final double maxError)
```

</div>

---

<div class="element-box">

### 🔧 calculateAvailability

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static double calculateAvailability(final long uptimeMs, final long totalTimeMs)
```

</div>

---

<div class="element-box">

### 🔧 formatMetricsSummary

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String formatMetricsSummary(final double successRate, final double errorRate, final double availability)
```

</div>

---

<div class="element-box">

### 🔧 measureTime

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static long measureTime(final Runnable operation)
```

</div>

---

<div class="element-box">

### 🔧 isWithinTimeLimit

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean isWithinTimeLimit(final long actualTime, final long limitMs)
```

</div>

---

<div class="element-box">

### 🔧 calculateThroughput

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static double calculateThroughput(final int elementCount, final long timeMs)
```

</div>

---

<div class="element-box">

### 🔧 batchElements

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static List<List<T>> batchElements(final List<T> elements, final int batchSize)
```

</div>

---

<div class="element-box">

### 🔧 estimateProcessingTime

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static long estimateProcessingTime(final int elementCount, final double avgTimePerElement)
```

</div>

---

<div class="element-box">

### 🔧 isCompletedWithinTimeout

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean isCompletedWithinTimeout(final CompletableFuture<?> future, final long timeoutMs)
```

</div>

---

<div class="element-box">

### 🔧 formatDuration

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String formatDuration(final long durationMs)
```

</div>

---

<div class="element-box">

### 🔧 calculateOptimalBatchSize

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static int calculateOptimalBatchSize(final int totalElements, final long maxMemoryMb)
```

</div>

---

<div class="element-box">

### 🔧 validatePerformanceMetrics

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean validatePerformanceMetrics(final double throughput, final long avgResponseTime, final double minThroughput, final long maxResponseTime)
```

</div>

---

<div class="element-box">

### 🔧 filterByType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static List<CodeElement> filterByType(final List<CodeElement> elements, final CodeElementType type)
```

</div>

---

<div class="element-box">

### 🔧 groupByType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static Map<CodeElementType, List<CodeElement>> groupByType(final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 isValidTimeout

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean isValidTimeout(final Integer timeoutMs)
```

</div>

---

<div class="element-box">

### 🔧 calculateAdaptiveTimeout

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static int calculateAdaptiveTimeout(final int elementCount)
```

</div>

---

<div class="element-box">

### 🔧 sanitizeFilePath

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String sanitizeFilePath(final String filePath)
```

</div>

---

<div class="element-box">

### 🔧 isSupportedDocType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean isSupportedDocType(final String docType)
```

</div>

---

<div class="element-box">

### 🔧 createDisplayName

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String createDisplayName(final CodeElement element)
```

</div>

---

<div class="element-box">

### 🔧 validateOperationParameters

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean validateOperationParameters(final String operation, final Map<String, Object> parameters)
```

</div>

---

<div class="element-box">

### 🔧 isRequiredParameter

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
private static boolean isRequiredParameter(final String operation, final Map<String, Object> parameters)
```

</div>

---

<div class="element-box">

### 🔧 formatErrorMessage

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String formatErrorMessage(final String serviceName, final String operation, final String cause)
```

</div>

---

<div class="element-box">

### 🔧 getRetryDelay

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static long getRetryDelay(final int attemptNumber)
```

</div>

---

<div class="element-box">

### 🔧 validateCodeElement

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean validateCodeElement(final CodeElement element)
```

</div>

---

<div class="element-box">

### 🔧 hasDuplicateNames

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean hasDuplicateNames(final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 countByType

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static long countByType(final List<CodeElement> elements, final CodeElementType type)
```

</div>

---

<div class="element-box">

### 🔧 hasMissingDocumentation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean hasMissingDocumentation(final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 getUniqueFilePaths

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static Set<String> getUniqueFilePaths(final List<CodeElement> elements)
```

</div>

---

<div class="element-box">

### 🔧 isValidOperation

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean isValidOperation(final String operation, final Object config)
```

</div>

---

<div class="element-box">

### 🔧 calculateCoverage

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static double calculateCoverage(final long covered, final long total)
```

</div>

---

<div class="element-box">

### 🔧 formatCoverage

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static String formatCoverage(final double coverage)
```

</div>

---

<div class="element-box">

### 🔧 meetsCoverageThreshold

#### 📄 Documentation

Error: LLM configuration is null. Please check the application configuration.

#### 💡 Usage Examples

Error: LLM configuration is null. Please check the application configuration.

#### 📋 Signature

```java
public static boolean meetsCoverageThreshold(final double coverage, final double threshold)
```

</div>

---

