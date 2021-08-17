#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import subprocess
import os
import re
import argparse
import shutil
import jsonpickle
from typing import Dict, Optional
from sys import platform

def resolve_class_paths(run_dir: str) -> str:
    jar_files = []
    for root, dirs, files in os.walk(run_dir):
        for file in files:
            path = os.path.join(root, file)
            if path.endswith(".jar"):
                jar_files.append(path)
    return ":".join(jar_files)


class Config(object):
    BASE_PATH = "fuzzer/core/third_party/l2switch/loopremover/implementation/"
    BASE_CLASS = "org.onosproject.fuzzer.driver."
    ONOS_DIR = os.path.join(os.path.dirname(os.path.realpath(__file__)), "../..")
    JQF_DIR = os.path.join(ONOS_DIR, "fuzzer/core/third_party/JQF")

    def __init__(self, class_name: str,
                 algo: str = "zest",
                 properties: Dict[str, str] = None,
                 start: int = 1,
                 step: int = 1,
                 debug_mode: bool = False,
                 onos_commit_hash: str = "",
                 jqf_commit_hash: str = ""):
        if properties is None:
            properties = {}
        self.class_name = class_name
        self.algo = algo
        self.properties = properties
        self.start = start
        self.step = step
        self.debug_mode = debug_mode
        self.onos_commit_hash = onos_commit_hash
        self.jqf_commit_hash = jqf_commit_hash

    def get_class_paths(self) -> str:
        return self.BASE_PATH+"target/classes:"+self.BASE_PATH+"target/test-classes:" + self.BASE_PATH + "/target/loopremover-impl-0.8.0-SNAPSHOT-jar-with-dependencies.jar"
        #  return resolve_class_paths(self.get_runfiles())

    def get_runfiles(self) -> str:
        return os.path.join(Config.BASE_PATH, self.class_name) + ".runfiles"

    def get_test_class(self) -> str:
        return "org.opendaylight.l2switch.loopremover.fuzz.drivers.LoopRemoverDriver"

    def get_java_home(self) -> str:
        if platform == "linux" or platform == "linux2":
            os_name = "linux"
        else:
            os_name = "macos"
        return ""
        #  return os.path.join(self.get_runfiles(), "remotejdk11_" + os_name)

    def get_work_dir(self) -> str:
        return os.path.join("results", self.class_name)

    def serialize(self, path: str):
        obj = jsonpickle.encode(self, indent=2)
        open(path, "w").write(obj)

    def debug_string(self) -> str:
        if self.debug_mode:
            return "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"
        else:
            return ""

    def checkout_or_store_commit_hash_process(self, repo_dir: str, commit: Optional[str]):
        repo = Repo(repo_dir)
        if commit:
            repo.checkout(commit)
        else:
            commit = repo.head.object.hexsha
        return commit

    def checkout_or_store_commit_hash(self):
        self.onos_commit_hash = self.checkout_or_store_commit_hash_process(Config.ONOS_DIR, self.onos_commit_hash)
        self.jqf_commit_hash = self.checkout_or_store_commit_hash_process(Config.JQF_DIR, self.jqf_commit_hash)

    def system_properties(self):
        properties = {
            "jqf.ei.PERFORMANCE_GUIDANCE": "true", 
            "jqf.tracing.MATCH_CALLEE_NAMES": "true", 
            ** self.properties
        }
        return " ".join(map(lambda it: f"-D{it[0]}={it[1]}", properties.items()))

    def run(self):
        shutil.rmtree(self.get_work_dir(), ignore_errors=True)
        os.makedirs(self.get_work_dir())
        #  self.checkout_or_store_commit_hash()
        self.serialize(os.path.join(self.get_work_dir(), "config.json"))
        print(["./fuzzer/core/third_party/JQF/bin/jqf-" + self.algo,
            "-v", "-c", self.get_class_paths(),
            self.get_test_class(), "zest", self.get_work_dir()])
        subprocess.run(["./fuzzer/core/third_party/JQF/bin/jqf-" + self.algo,
            "-v", "-c", self.get_class_paths(),
            self.get_test_class(), "zest", self.get_work_dir()],
            timeout=60*60,
            env={
                "INTERESTING_FIELD_STEP": str(self.step),
                "INTERESTING_FIELD_START": str(self.start),
                "JQF_CONFIG": self.debug_string() + " " + self.system_properties(),
                "JAVA_HOME": self.get_java_home()
                })



if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    modes = parser.add_subparsers(dest="mode")

    config_mode = modes.add_parser("config")
    config_mode.add_argument("path")
    config_mode.add_argument("--start")
    config_mode.add_argument("--step")
    config_mode.add_argument("--debug", default=False, action="store_true")

    command_mode = modes.add_parser("command")
    command_mode.add_argument("algo")
    command_mode.add_argument("class_name")

    command_mode.add_argument("--step", default=1)
    command_mode.add_argument("--start", default=1)
    command_mode.add_argument("--onos_commit_hash", default="")
    command_mode.add_argument("--jqf_commit_hash", default="")
    command_mode.add_argument("--debug", default=False, action="store_true")

    args = parser.parse_args()

    if args.mode == "config":
        config = jsonpickle.decode(open(args.path).read())
        if args.start:
            config.start = args.start
        if args.step:
            config.step = args.step
        config.debug_mode = args.debug
    else:
        config = Config(args.class_name, args.algo, {}, args.start,
                        args.step, args.debug)
    config.run()
