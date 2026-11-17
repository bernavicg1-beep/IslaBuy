#!/usr/bin/env python3
"""Verification gate for the IslaBuy closed loop.

Runs the real Android/Gradle test suite. Blocks the agent stop (exit 2 +
decision:block JSON) while tests are red, so the loop keeps fixing until green.

Never substitute the agent's self-assessment for this check.
"""
import json
import os
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
GRADLEW = os.path.join(ROOT, "gradlew")
GRADLE = "gradle"

# Set up environment for Android tooling installed in ~/tools and ~/android-sdk
os.environ.setdefault("JAVA_HOME", os.path.expanduser("~/tools/jdk-17.0.13+11"))
os.environ.setdefault("ANDROID_HOME", os.path.expanduser("~/android-sdk"))
os.environ.setdefault("ANDROID_SDK_ROOT", os.path.expanduser("~/android-sdk"))
os.environ["PATH"] = (
    os.path.join(os.environ["JAVA_HOME"], "bin") + ":"
    + os.path.expanduser("~/tools/gradle-8.4/bin") + ":"
    + os.path.join(os.environ["ANDROID_HOME"], "cmdline-tools", "latest", "bin") + ":"
    + os.path.join(os.environ["ANDROID_HOME"], "platform-tools") + ":"
    + os.environ.get("PATH", "")
)

# Prefer the project wrapper; fall back to a system gradle if present.
if os.path.isfile(GRADLEW) and os.access(GRADLEW, os.X_OK):
    cmd = [GRADLEW, "test", "--offline", "--stacktrace"]
elif subprocess.run(["which", GRADLE], capture_output=True).returncode == 0:
    cmd = [GRADLE, "test", "--offline", "--stacktrace"]
else:
    # No Android tooling available in this environment. Block honestly rather
    # than pretending success.
    print(json.dumps({
        "decision": "block",
        "reason": (
            "Android tooling (JDK/Gradle/Android SDK) is not available in this "
            "environment, so the verification gate cannot run. Install the JDK, "
            "Gradle wrapper, and Android SDK before relying on this loop. "
            "Do not declare the task done without a green gate."
        ),
    }))
    sys.exit(2)

result = subprocess.run(cmd, cwd=ROOT, capture_output=True, text=True)

if result.returncode != 0:
    # Surface the tail of the output so the agent can see the failure.
    tail = (result.stdout + "\n" + result.stderr).strip().splitlines()[-40:]
    reason = "Gradle tests are RED. Keep fixing until green.\n" + "\n".join(tail)
    print(json.dumps({"decision": "block", "reason": reason}))
    sys.exit(2)

sys.exit(0)
