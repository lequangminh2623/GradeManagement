import { Container } from "react-bootstrap";

const Footer = () => {
    return (
        <footer className="bg-light text-center text-muted py-3 mt-auto" style={{ height: "6vh" }} >
            <Container>
                <small>&copy; {new Date().getFullYear()} Grade Management</small>
            </Container>
        </footer>
    );
};

export default Footer;
