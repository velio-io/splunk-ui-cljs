/// <reference types="cypress" />

describe('testing actions with key-value control', () => {
    beforeEach(() => {
        cy.visit('http://localhost:6006/iframe.html?id=flow--simple-flow&viewMode=story')
            .wait(500)

        cy.get('.react-flow__node-action')
            .click()
            .find('[data-test="edit-action"]')
            .click()

        cy.get('.react-flow__node-action')
            .find('button[data-test=select]')
            .click()

        cy.wait(200)
    })

    afterEach(() => {
        cy.get('.react-flow__node-action')
            .find('button[type=submit]')
            .click()

        cy.get('.react-flow__node-action')
            .should('have.text', 'Test')
    })

    it('default action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('default')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-name"] input')
            .type('my-key')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-value"] input')
            .type('my-value')

        cy.get('.react-flow__node-action')
            .find('button[data-test="button"]')
            .contains('Add')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .should('have.text', 'my-key : ')

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .should('have.text', 'my-value')

        cy.get('button[data-test="button"]')
            .should('not.exist')
    })

    it('rename-keys action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('rename-keys')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-name"] input')
            .type('first-key')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-value"] input')
            .type('first-value')

        cy.get('.react-flow__node-action')
            .find('button[data-test="button"]')
            .contains('Add')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-name"] input')
            .type('second-key')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-value"] input')
            .type('second-value')

        cy.get('.react-flow__node-action')
            .find('button[data-test="button"]')
            .contains('Add')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .should('have.length', 2)

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .first()
            .should('have.text', 'first-key : ')

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .last()
            .should('have.text', 'second-key : ')

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .should('have.length', 2)

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .first()
            .should('have.text', 'first-value')

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .last()
            .should('have.text', 'second-value')
    })

    it('with action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('with')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-name"] input')
            .type('first-key')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-value"] input')
            .type('first-value')

        cy.get('.react-flow__node-action')
            .find('button[data-test="button"]')
            .contains('Add')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-name"] input')
            .type('second-key')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key-value"] input')
            .type('second-value')

        cy.get('.react-flow__node-action')
            .find('button[data-test="button"]')
            .contains('Add')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .should('have.length', 2)

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .first()
            .should('have.text', 'first-key : ')

        cy.get('.react-flow__node-action')
            .find('[data-test="term"]')
            .last()
            .should('have.text', 'second-key : ')

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .should('have.length', 2)

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .first()
            .should('have.text', 'first-value')

        cy.get('.react-flow__node-action')
            .find('[data-test="description"]')
            .last()
            .should('have.text', 'second-value')
    })
})
