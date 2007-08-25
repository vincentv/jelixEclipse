require 'jbPage'
require 'jbIndex'
require 'ftools'

class JbManuel
  def initialize remoteIndex, localDir
    puts "Initialisation du bot"
    @remoteIndex = remoteIndex
    @localDir = localDir
  end
  
  def start
    puts "Demarrage..."
    start = Time.now
    
    puts "Création de l'arborescence"
    File.makedirs(@localDir + '/html/manuel')
    File.copy('design.css', @localDir + '/html/design.css')
    File.copy('button-cc.gif', @localDir + '/html/button-cc.gif')
    
    puts "Récupération de la documentation"
    createDocumentation
    
    puts "Création de l'index"
    createIndex
    
    puts "Documentation locale terminée"
    puts "Documentation générée en #{Time.now - start}"
  end
  
  private 
  
  def createDocumentation
    page = JbPage.new @remoteIndex, @localDir
    page.parse
  end
  
  def createIndex
    index = JbIndex.new @remoteIndex, @localDir
    index.parse
  end
end
