FROM ubuntu:17.10

ENV ANDROID_SDK_HOME /opt/android-sdk-linux
ENV ANDROID_SDK_ROOT /opt/android-sdk-linux
ENV ANDROID_HOME /opt/android-sdk-linux
ENV ANDROID_SDK /opt/android-sdk-linux

ENV PATH "${PATH}:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/emulator:${ANDROID_HOME}/bin"

ENV DEBIAN_FRONTEND noninteractive

# Set Locale

RUN apt-get clean && apt-get -y update && apt-get install -y locales && locale-gen en_US.UTF-8
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

# Install required tools
# Dependencies to execute Android builds

RUN dpkg --add-architecture i386 && apt-get update -yqq && apt-get install -y \
  curl \
  expect \
  git \
  libc6:i386 \
  libgcc1:i386 \
  libncurses5:i386 \
  libstdc++6:i386 \
  zlib1g:i386 \
  openjdk-8-jdk \
  wget \
  unzip \
  vim \
  && apt-get clean

# Install required tools
# Dependencies for fastlane

RUN apt-get update && apt-get install --no-install-recommends -y build-essential git ruby2.3-dev \
  && gem install fastlane \
  && gem install bundler \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* \
  && apt-get autoremove -y && apt-get clean

RUN groupadd android && useradd -d /opt/android-sdk-linux -g android android

RUN ls -la \
  && cd opt/ \
  && ls -la

WORKDIR /opt/android-sdk-linux

RUN /opt/tools/entrypoint.sh built-in

RUN /opt/android-sdk-linux/tools/bin/sdkmanager "build-tools;27.0.3"

RUN /opt/android-sdk-linux/tools/bin/sdkmanager "platforms;android-26"

RUN /opt/android-sdk-linux/tools/bin/sdkmanager "system-images;android-26;google_apis;x86_64"

CMD /opt/tools/entrypoint.sh built-in
