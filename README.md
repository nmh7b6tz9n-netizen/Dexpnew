# DEXP OS 2.1 — Android TV launcher source

This is Android source code, not an installable APK. GitHub Actions builds the APK.

## GitHub (iPad / iPhone)

1. Create a new empty repository on GitHub.
2. Extract this ZIP in Codespaces, then copy all contents of DEXP-OS-2 into the repository root (including `.github`).
3. Commit and push the changes to `main`.
4. Open GitHub → Actions → Build DEXP OS APK and download the `DEXP-OS-APK` artifact after a successful build.
5. Extract the artifact ZIP to get `app-debug.apk`.

This project uses Android Gradle Plugin 8.7.3, Gradle 8.9 and JDK 17. It uses the SDK installed on GitHub's Ubuntu runner. It has not been tested on the DEXP H39F8000Q.

The launcher can be opened as an app. Whether it can replace the TV home screen depends on firmware restrictions.
