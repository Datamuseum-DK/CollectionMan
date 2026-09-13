# Release procedure

## Development work

The project is too small to use branches or pull requests, and the
procedure below is a reflection of this.

When working on new features, set the version number for the project in the
`pom.xml` to the next release and `-dev` as a suffix.

You can push your changes to the default branch when needed. The application
must build and the tests execute successfully.

When ready to release you remove the `-dev` from the version number. This
command should give you the intended version for the release:

    xpath -q -e "/project/version/text()" pom.xml

Then you build the application:

    mvn clean install

If you deploy with containers you can also build the image:

    podman build . -t sorenroug/mobilereg:latest -f Containerfile

Then you push all the changes to GitHub and tag it with the version number.

    git push
    VERSION=$(xpath -q -e "/project/version/text()" pom.xml)
    git tag -a v$(VERSION) -m "Release $(VERSION)"
    git push origin v$(VERSION)

