./gradlew \
    clean \
    lintRelease \
    assembleRelease \
    generateArchives \
    generatePomFileForAarPublication

open fastscroll/build/reports/lint-results-release.html
