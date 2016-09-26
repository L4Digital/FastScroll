./gradlew \
    clean \
    lintRelease \
    assembleRelease \
    generateArchives \
    generatePomFileForAarPublication

open fastscroll/build/outputs/lint-results-release.html
